/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.modeler.client.widget.propertyform;

import java.util.ArrayList;

import org.openremote.modeler.client.utils.ImageSourceValidator;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.NavigateFieldSet;
import org.openremote.modeler.client.widget.component.ImageUploadAdapterField;
import org.openremote.modeler.client.widget.component.ScreenTabbarItem;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanel;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.component.ImageSource;
import org.openremote.modeler.domain.component.Navigate;
import org.openremote.modeler.domain.component.Navigate.ToLogicalType;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FieldSetEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * A panel for display screen button properties.
 */
public class TabbarItemPropertyForm extends PropertyForm {
   private NavigateFieldSet navigateSet = null;
   private ScreenTabbarItem screenTabbarItem = null;
   
   public TabbarItemPropertyForm(ScreenTabbarItem screenTabbarItem) {
      super(screenTabbarItem);
      this.screenTabbarItem = screenTabbarItem;
      addFields();
      addSubmitListenersToForm();
      super.addDeleteButton();
   }
   private void addFields() {
      // initial name field.
      final TextField<String> name = new TextField<String>();
      name.setFieldLabel("Name");
      name.setValue(screenTabbarItem.getName());
      name.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            screenTabbarItem.setName(name.getValue());
         }
      });
      
      final ImageUploadAdapterField imageUploaderField = new ImageUploadAdapterField(null);
      imageUploaderField.addUploadListener(Events.OnChange, new Listener<FieldEvent>() {
         public void handleEvent(FieldEvent be) {
            if (!isValid()) {
               return;
            }
            submit();
         }
      });
      
      imageUploaderField.addDeleteListener(new SelectionListener<ButtonEvent>() {
         public void componentSelected(ButtonEvent ce) {
            if (screenTabbarItem.getImageSource().getSrc() != null) {
               screenTabbarItem.removeImage();
               WidgetSelectionUtil.setSelectWidget(null);
               WidgetSelectionUtil.setSelectWidget(screenTabbarItem);
            }
         }
         
      });
      imageUploaderField.setImage(screenTabbarItem.getImageSource().getSrc());
      imageUploaderField.setFieldLabel("Image Source");
      imageUploaderField.setActionToForm(this);
      // initial navigate properties
      final Navigate navigate = screenTabbarItem.getNavigate();
      Group parentGroup = screenTabbarItem.getScreenCanvas().getScreen().getScreenPair().getParentGroup();
      if (parentGroup != null) {
         navigateSet = new NavigateFieldSet(navigate, parentGroup.getParentPanel().getGroups());
      } else {
         navigateSet = new NavigateFieldSet(navigate, new ArrayList<Group>());
      }
      navigateSet.setCheckboxToggle(true);
      navigateSet.addListener(Events.BeforeExpand, new Listener<FieldSetEvent>() {
         @Override
         public void handleEvent(FieldSetEvent be) {
            if (!navigate.isSet()) {
               navigate.setToLogical(ToLogicalType.login);
            }
            navigateSet.update(navigate);
         }
         
      });
      navigateSet.addListener(Events.BeforeCollapse, new Listener<FieldSetEvent>() {
         @Override
         public void handleEvent(FieldSetEvent be) {
            navigate.clear();
         }
      });
      if (navigate.isSet()) {
         navigateSet.fireEvent(Events.BeforeExpand);
      } else {
         navigateSet.collapse();
      }
      
      add(name);
      add(imageUploaderField);
      add(navigateSet);
      
   }
   
   private void addSubmitListenersToForm() {
      addListener(Events.Submit, new Listener<FormEvent>() {
         @Override
         public void handleEvent(FormEvent be) {
            String backgroundImgURL = ImageSourceValidator.validate(be.getResultHtml());
            boolean success = !"".equals(backgroundImgURL);
            if (success) {
               screenTabbarItem.setImageSource(new ImageSource(backgroundImgURL));
            }
         }
      });
   }
   
   @Override
   protected void afterRender() {
      super.afterRender();
      ((PropertyPanel)this.getParent()).setHeading("Tab bar item properties");
   }
}
