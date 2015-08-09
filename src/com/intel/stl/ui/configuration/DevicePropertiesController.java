/**
 * INTEL CONFIDENTIAL
 * Copyright (c) 2014 Intel Corporation All Rights Reserved.
 * The source code contained or described herein and all documents related to the source code ("Material")
 * are owned by Intel Corporation or its suppliers or licensors. Title to the Material remains with Intel
 * Corporation or its suppliers and licensors. The Material contains trade secrets and proprietary and
 * confidential information of Intel or its suppliers and licensors. The Material is protected by
 * worldwide copyright and trade secret laws and treaty provisions. No part of the Material may be used,
 * copied, reproduced, modified, published, uploaded, posted, transmitted, distributed, or disclosed in
 * any way without Intel's prior express written permission. No license under any patent, copyright,
 * trade secret or other intellectual property right is granted to or conferred upon you by disclosure
 * or delivery of the Materials, either expressly, by implication, inducement, estoppel or otherwise.
 * Any license under such intellectual property rights must be express and approved by Intel in writing.
 */

/*******************************************************************************
 *                       I N T E L   C O R P O R A T I O N
 *	
 *  Functional Group: Fabric Viewer Application
 *
 *  File Name: BasicPropertyController.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.4  2015/04/03 21:06:33  jijunwan
 *  Archive Log:    Introduced canExit to IPageController, and canPageChange to IPageListener to allow us do some checking before we switch to another page. Fixed the following bugs
 *  Archive Log:    1) when we refresh, do not show login dialog if Admin is not the current page
 *  Archive Log:    2) confirm abandon if we switch from admin page to other pages and there is changes on the Admin page
 *  Archive Log:    3) confirm abandon in Admin page if we switch between Application, DeviceGroup and VirtualFabric
 *  Archive Log:    4) added null check to handle special cases
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2015/04/02 13:32:57  jypak
 *  Archive Log:    Klockwork: Front End Critical Without Unit Test. 47 open issues fixed. All of them are for null checks.
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/10/23 16:00:05  jijunwan
 *  Archive Log:    changed topology information display to use device property panels, and JSectionView
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/10/22 01:53:40  jijunwan
 *  Archive Log:    renamed BasePropertiesController to DevicePropertiesController
 *  Archive Log:
 *  Archive Log:    Revision 1.14  2014/10/16 13:16:39  fernande
 *  Archive Log:    Changes to AbstractTask to support an onFinally method that is guaranteed to be called no matter what happens in the onTaskSuccess and onTaskFailure implementations for a task.
 *  Archive Log:
 *  Archive Log:    Revision 1.13  2014/10/13 21:06:15  fernande
 *  Archive Log:    Changed GetDevicePropertiesTask to be driven by the PropertiesDisplayOptions in UserSettings instead of hard coded
 *  Archive Log:
 *  Archive Log:    Revision 1.11  2014/10/09 13:04:52  fernande
 *  Archive Log:    Adding IContextAware interface to generalize setting up Context
 *  Archive Log:
 *  Archive Log:    Revision 1.10  2014/09/15 15:24:33  jijunwan
 *  Archive Log:    changed AppEventBus to 3rd party lib mbassador
 *  Archive Log:    some code reformat
 *  Archive Log:
 *  Archive Log:    Revision 1.9  2014/09/04 16:54:26  jijunwan
 *  Archive Log:    added code to support changing property viz style through UI
 *  Archive Log:
 *  Archive Log:    Revision 1.8  2014/09/02 19:24:31  jijunwan
 *  Archive Log:    renamed FVTreeBuilder to tree.FVTreeManager, moved FVResourceNode and FVTreeModel  to package tree
 *  Archive Log:
 *  Archive Log:    Revision 1.7  2014/08/26 15:15:36  jijunwan
 *  Archive Log:    added refresh function to all pages
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2014/08/22 16:52:48  fernande
 *  Archive Log:    Closing the gaps between properties and sa_query
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2014/08/18 21:30:27  fernande
 *  Archive Log:    Adding more properties for display
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2014/08/14 17:37:20  fernande
 *  Archive Log:    Closing the gap on device properties being displayed.
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/08/04 21:16:35  fernande
 *  Archive Log:    Changed DeviceProperties to be more extensible and support adding more property categories and property groups
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/07/28 15:41:45  fernande
 *  Archive Log:    Made the GetDevicePropertiesTask available for testing purposes
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/07/25 20:31:08  fernande
 *  Archive Log:    New property controllers
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: fernande
 *
 ******************************************************************************/

package com.intel.stl.ui.configuration;

import static com.intel.stl.ui.common.PageWeight.MEDIUM;
import static com.intel.stl.ui.common.STLConstants.K0300_PROPERTIES;
import static com.intel.stl.ui.common.STLConstants.K0301_PROPERTIES_DESCRIPTION;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

import javax.swing.ImageIcon;
import javax.swing.Timer;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.api.configuration.PropertyGroup;
import com.intel.stl.api.configuration.ResourceType;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.ui.common.FinishObserver;
import com.intel.stl.ui.common.IPerfSubpageController;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.ObserverAdapter;
import com.intel.stl.ui.common.PageWeight;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.configuration.view.DevicePropertiesPanel;
import com.intel.stl.ui.configuration.view.DevicePropertyGroupPanel;
import com.intel.stl.ui.configuration.view.IPropertyListener;
import com.intel.stl.ui.configuration.view.PropertyVizStyle;
import com.intel.stl.ui.framework.AbstractController;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.model.DeviceProperties;
import com.intel.stl.ui.model.DevicePropertyGroup;
import com.intel.stl.ui.monitor.PerformanceTreeController;
import com.intel.stl.ui.monitor.TreeNodeType;
import com.intel.stl.ui.monitor.tree.FVResourceNode;

public class DevicePropertiesController
        extends
        AbstractController<DeviceProperties, DevicePropertiesPanel, DevicePropertiesController>
        implements IPerfSubpageController, IPropertyListener {
    private GetDevicePropertiesTask propertiesTask;

    private Timer viewClearTimer;

    private UserSettings userSettings;

    private FVResourceNode currentNode;

    private PerformanceTreeController parentController;

    private final PropertyVizStyle style = new PropertyVizStyle();

    public DevicePropertiesController(DeviceProperties model,
            DevicePropertiesPanel view, MBassador<IAppEvent> eventBus) {
        super(model, view, eventBus);
    }

    @Override
    public void initModel() {
    }

    @Override
    public void setContext(Context context, IProgressObserver observer) {
        try {
            super.setContext(context);
            this.userSettings = context.getUserSettings();
        } finally {
            observer.onFinish();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.common.IPageController#onRefresh(com.intel.stl.ui.common
     * .IProgressObserver)
     */
    @Override
    public void onRefresh(IProgressObserver observer) {
        if (observer == null) {
            observer = new ObserverAdapter();
        }

        if (currentNode != null) {
            showNode(currentNode, observer);
        }

        observer.onFinish();
    }

    // We override the default behavior to control the building of the
    // different pieces in the view
    @Override
    public void onTaskSuccess() {
        clearTimer();
        view.clearPanel();
        for (DevicePropertyGroup group : model.getGroups()) {
            DevicePropertyGroupPanel groupPanel =
                    new DevicePropertyGroupPanel(style);
            new PropertyGroupController(group, groupPanel, eventBus);
            view.addPropertyGroupPanel(groupPanel);
        }
        List<PropertyGroup> groups =
                userSettings.getPropertiesDisplayOptions().get(
                        model.getResourceType());
        if (groups != null) {
            view.initUserSettings(groups);
        }
        notifyModelChanged();
    }

    @Override
    public void onTaskFailure(Throwable caught) {
        clearTimer();
        notifyModelUpdateFailed(caught);
    }

    @Override
    public String getName() {
        return K0300_PROPERTIES.getValue();
    }

    @Override
    public String getDescription() {
        return K0301_PROPERTIES_DESCRIPTION.getValue();
    }

    @Override
    public Component getView() {
        return view;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getIcon()
     */
    @Override
    public ImageIcon getIcon() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#cleanup()
     */
    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#onEnter()
     */
    @Override
    public void onEnter() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#onExit()
     */
    @Override
    public void onExit() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#canExit()
     */
    @Override
    public boolean canExit() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#clear()
     */
    @Override
    public void clear() {
        if (viewClearTimer == null) {
            viewClearTimer =
                    new Timer(UIConstants.UPDATE_TIME, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (viewClearTimer != null) {
                                view.clear();
                            }
                        }
                    });
            viewClearTimer.setRepeats(false);
        }
        viewClearTimer.restart();
    }

    @Override
    public void showNode(FVResourceNode node, IProgressObserver observer) {
        if (propertiesTask != null) {
            if (propertiesTask.isSubmitted() && !propertiesTask.isDone()) {
                try {
                    propertiesTask.cancel(true);
                } catch (CancellationException e) {
                    // Ignore exception since this is what we expect
                }
            }
        }
        ResourceType resourceType =
                TreeNodeType.getResourceTypeFor(node.getType());
        if (resourceType == null) {
            currentNode = node;
            return;
        }
        // This clears all categories selected in the model
        model.setResourceType(resourceType);

        propertiesTask = new GetDevicePropertiesTask(model, node, observer);
        submitTask(propertiesTask);
        currentNode = node;
    }

    @Override
    public void setParentController(PerformanceTreeController parentController) {
        this.parentController = parentController;
    }

    protected void clearTimer() {
        if (viewClearTimer != null) {
            if (viewClearTimer.isRunning()) {
                viewClearTimer.stop();
            }
            viewClearTimer = null;
        }
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    @Override
    public PageWeight getContextSwitchWeight() {
        return MEDIUM;
    }

    @Override
    public PageWeight getRefreshWeight() {
        return MEDIUM;
    }

    // for testing
    protected GetDevicePropertiesTask getTask() {
        return propertiesTask;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.configuration.view.IPropertyStyleListener#onShowBorder
     * (boolean)
     */
    @Override
    public void onShowBorder(boolean isSelected) {
        style.setShowBorder(isSelected);
        onTaskSuccess();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.configuration.view.IPropertyStyleListener#showAlternation
     * (boolean)
     */
    @Override
    public void onShowAlternation(boolean isSelected) {
        style.setAlternateRows(isSelected);
        onTaskSuccess();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.configuration.view.IPropertyListener#onDisplayChanged
     * (java.util.Map)
     */
    @Override
    public void onDisplayChanged(Map<String, Boolean> newSelections) {
        if (applyChange(newSelections)) {
            if (parentController != null) {
                parentController.setRunning(true);
                showNode(currentNode, new FinishObserver() {
                    @Override
                    public void onFinish() {
                        parentController.setRunning(false);
                    }
                });
            } else {
                showNode(currentNode, null);
            }
        }
    }

    protected boolean applyChange(Map<String, Boolean> newSelections) {
        boolean changed = false;
        List<PropertyGroup> groups =
                userSettings.getPropertiesDisplayOptions().get(
                        model.getResourceType());
        if (groups != null) {
            for (PropertyGroup group : groups) {
                Boolean newSelection = newSelections.get(group.getName());
                if (newSelection != null
                        && !newSelection.equals(group.isDisplayed())) {
                    changed = true;
                    group.setDisplayed(newSelection);
                }
            }
        }
        return changed;
    }
}