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
package org.openremote.modeler.client.widget.component;

import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.utils.PropertyEditable;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.client.widget.propertyform.ScreenPropertyEditForm;
import org.openremote.modeler.client.widget.uidesigner.ScreenTab;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

/**
 * 
 * @author Javen
 *
 */
public class ScreenPropertyEditable implements PropertyEditable {
   
   private static final long serialVersionUID = -1171027894552188432L;

   private ScreenPair screen = null;
   private ScreenPairRef screenPairRef = null;

   private TreePanel<BeanModel> profileTree = null;

   private ScreenTab screenTab = null;
   
   public ScreenPropertyEditable() {
   }

   public ScreenPropertyEditable(ScreenPairRef screenPairRef, TreePanel<BeanModel> profileTree) {
      this.screenPairRef = screenPairRef;
      this.screen = screenPairRef.getScreen();
      this.profileTree = profileTree;
   }

   public void setName(String name) {
      if (screen != null && name.trim().length() != 0) {
         screen.setName(name);
         updateScreen();
      }
   }

   public String getName() {
      if (screen != null) {
         return screen.getName();
      } else {
         return "";
      }
   }

   public void setScreenTab(ScreenTab screenTab) {
      this.screenTab = screenTab;
   }
   
   @Override
   public PropertyForm getPropertiesForm() {
      return new ScreenPropertyEditForm(this, screenPairRef.getScreen(), screenTab);
   }

   private void updateScreen() {
      profileTree.getStore().update(screenPairRef.getBeanModel());
      BeanModelDataBase.screenTable.update(screen.getBeanModel());
   }

   @Override
   public String getTitle() {
      return "Screen Property";
   }
   
}
