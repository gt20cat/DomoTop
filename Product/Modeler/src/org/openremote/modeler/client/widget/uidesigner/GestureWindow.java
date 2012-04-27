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
package org.openremote.modeler.client.widget.uidesigner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.utils.DeviceAndMacroTree;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.widget.NavigateFieldSet;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.DeviceMacroRef;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.component.Gesture;
import org.openremote.modeler.domain.component.Gesture.GestureType;
import org.openremote.modeler.domain.component.Navigate.ToLogicalType;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldSetEvent;
import com.extjs.gxt.ui.client.event.ListViewEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.CheckBoxListView;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

public class GestureWindow extends Dialog {

   private List<Gesture> gestures;
   private List<Group> groups;
   private CheckBoxListView<BeanModel> gestureTypeListView;
   private TreePanel<BeanModel> devicesAndMacrosTree;
   private Gesture selectedGesture = new Gesture(GestureType.swipe_left_to_right);
   private Map<String, Gesture> gestureMaps = new HashMap<String, Gesture>();
   private final String SELECTED_COMMAND = "Selected command: ";
   public GestureWindow(List<Gesture> gestures, List<Group> groups) {
      this.gestures = gestures;
      this.groups = groups;
      initial();
      show();
   }
   
   private void initial() {
      setHeading("Config gestures");
      setMinHeight(400);
      setMinWidth(440);
      setModal(true);
      setLayout(new BorderLayout());
      setButtons(Dialog.OKCANCEL);  
      setHideOnButtonClick(true);
      setBodyBorder(false);
      
      addListener(Events.BeforeHide, new Listener<WindowEvent>() {
         public void handleEvent(WindowEvent be) {
            if (be.getButtonClicked() == getButtonById("ok")) {
               List<BeanModel> checkedGestures = gestureTypeListView.getChecked();
               gestures.clear();
               for (BeanModel beanModel : checkedGestures) {
                  gestures.add(gestureMaps.get(((Gesture)beanModel.getBean()).getType().toString()));
               }
               fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(gestures));
            }
            devicesAndMacrosTree.getSelectionModel().removeAllListeners();
         }
      });
      createGestureTypeList();
      createGesturePropertyForm();
   }
   private void createGestureTypeList() {
      ContentPanel gestureTypesContainer = new ContentPanel();
      gestureTypesContainer.setHeaderVisible(false);
      gestureTypesContainer.setLayout(new FitLayout());
      // overflow-auto style is for IE hack.
      gestureTypesContainer.addStyleName("overflow-auto");
      gestureTypesContainer.setBorders(false);
      gestureTypesContainer.setBodyBorder(false);
      
      gestureTypeListView = new CheckBoxListView<BeanModel>();
      
      ListStore<BeanModel> gestureStore = new ListStore<BeanModel>();
      GestureType[] gestureTypes = GestureType.values();
      for (int i = 0; i < gestureTypes.length; i++) {
         Gesture gesture = new Gesture(gestureTypes[i]);
         gesture.setOid(IDUtil.nextID());
         gestureMaps.put(gestureTypes[i].toString(), gesture);
         gestureStore.add(gesture.getBeanModel());
         for (Gesture existGesture : gestures) {
            if (gestureTypes[i].equals(existGesture.getType())) {
               gestureMaps.put(existGesture.getType().toString(), existGesture);
               gestureTypeListView.setChecked(gesture.getBeanModel(), true);
            }
         }
      }
      gestureTypeListView.setStore(gestureStore);
      gestureTypeListView.setDisplayProperty("type");
      gestureTypeListView.setStyleAttribute("overflow", "auto");
      gestureTypesContainer.add(gestureTypeListView);
      
      BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 150);
      westData.setMargins(new Margins(0, 5, 0, 0));
      add(gestureTypesContainer, westData);
   }
   
   private void createGesturePropertyForm() {
      FormPanel gesturePropertyForm = new FormPanel();
      gesturePropertyForm.setLabelAlign(LabelAlign.TOP);
      gesturePropertyForm.setHeaderVisible(false);
      
      final Text selectedCommand = new Text(SELECTED_COMMAND);
      
      ContentPanel commandTreeContainer = new ContentPanel();
      commandTreeContainer.setHeaderVisible(false);
      commandTreeContainer.setBorders(false);
      commandTreeContainer.setBodyBorder(false);
      commandTreeContainer.setSize(240, 150);
      commandTreeContainer.setLayout(new FitLayout());
      commandTreeContainer.setScrollMode(Scroll.AUTO);
      if (devicesAndMacrosTree == null) {
         devicesAndMacrosTree = DeviceAndMacroTree.getInstance();
         commandTreeContainer.add(devicesAndMacrosTree);
      }
      devicesAndMacrosTree.collapseAll();
      final AdapterField commandField = new AdapterField(commandTreeContainer);
      commandField.setFieldLabel("Select a command");
      commandField.setBorders(true);
      devicesAndMacrosTree.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {
         public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
            BeanModel commandModel = se.getSelectedItem();
            if (commandModel.getBean() != null) {
               UICommand uiCommand = null;
               String commandName = SELECTED_COMMAND;
               if (commandModel.getBean() instanceof DeviceCommand) {
                  uiCommand = new DeviceCommandRef((DeviceCommand) commandModel.getBean());
                  commandName = SELECTED_COMMAND + uiCommand.getDisplayName();
               } else if (commandModel.getBean() instanceof DeviceMacro) {
                  uiCommand = new DeviceMacroRef((DeviceMacro) commandModel.getBean());
                  commandName = SELECTED_COMMAND + uiCommand.getDisplayName();
               }
               selectedCommand.setText(commandName);
               selectedGesture.setUiCommand(uiCommand);
            }
         }
      });
      
      final NavigateFieldSet navigateSet = new NavigateFieldSet(selectedGesture.getNavigate(), groups);
      navigateSet.setStyleAttribute("marginTop", "10px");
      navigateSet.setCheckboxToggle(true);
      navigateSet.addListener(Events.BeforeExpand, new Listener<FieldSetEvent>() {
         @Override
         public void handleEvent(FieldSetEvent be) {
            if (!selectedGesture.getNavigate().isSet()) {
               selectedGesture.getNavigate().setToLogical(ToLogicalType.setting);
            }
            navigateSet.update(selectedGesture.getNavigate());
         }
         
      });
      navigateSet.addListener(Events.BeforeCollapse, new Listener<FieldSetEvent>() {
         @Override
         public void handleEvent(FieldSetEvent be) {
            selectedGesture.getNavigate().clear();
         }
      });
      navigateSet.collapse();
      
      gesturePropertyForm.add(commandField);
      gesturePropertyForm.add(selectedCommand);
      gesturePropertyForm.add(navigateSet);
      BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
      add(gesturePropertyForm, centerData);
      
      gestureTypeListView.addListener(Events.Select, new Listener<ListViewEvent<BeanModel>>() {
         public void handleEvent(ListViewEvent<BeanModel> be) {
            gestureTypeListView.setChecked(be.getModel(), true);
            Gesture gesture = gestureMaps.get(((Gesture)be.getModel().getBean()).getType().toString());
            if (!gesture.equals(selectedGesture)) {
               selectedGesture = gesture;
               devicesAndMacrosTree.collapseAll();
               if (selectedGesture.getUiCommand() != null) {
                  UICommand uiCommnad = selectedGesture.getUiCommand();
                  if (uiCommnad instanceof DeviceCommandRef) {
                     selectedCommand.setText(SELECTED_COMMAND + ((DeviceCommandRef) uiCommnad).getDisplayName());
                  } else if (uiCommnad instanceof DeviceMacroRef) {
                     selectedCommand.setText(SELECTED_COMMAND + ((DeviceMacroRef) uiCommnad).getDisplayName());
                  }
               } else {
                  selectedCommand.setText(SELECTED_COMMAND);
               }
               if (selectedGesture.getNavigate().isSet()) {
                  navigateSet.expand();
                  navigateSet.fireEvent(Events.BeforeExpand);
               } else {
                  navigateSet.collapse();
               }
            }
         }
      });
   }
}
