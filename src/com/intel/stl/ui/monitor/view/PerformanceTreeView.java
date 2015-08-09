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
 *  File Name: PerformanceTreeView.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.17  2015/04/10 20:19:04  fernande
 *  Archive Log:    Changed TopologyView to be passed two background services (graphService and outlineService) which now reside in FabricController and can be properly shutdown when an error occurs.
 *  Archive Log:
 *  Archive Log:    Revision 1.16  2014/10/09 21:24:50  jijunwan
 *  Archive Log:    improvement on TreeNodeType:
 *  Archive Log:    1) Added icon to TreeNodeType
 *  Archive Log:    2) Rename PORT to ACTIVE_PORT
 *  Archive Log:    3) Removed NODE
 *  Archive Log:
 *  Archive Log:    Revision 1.15  2014/09/02 19:24:32  jijunwan
 *  Archive Log:    renamed FVTreeBuilder to tree.FVTreeManager, moved FVResourceNode and FVTreeModel  to package tree
 *  Archive Log:
 *  Archive Log:    Revision 1.14  2014/07/18 13:38:39  rjtierne
 *  Archive Log:    Changed scope of class attributes to private
 *  Archive Log:
 *  Archive Log:    Revision 1.13  2014/07/03 22:21:25  jijunwan
 *  Archive Log:    extended TreeController and TreeView to support multi-selection and programmly operate a tree
 *  Archive Log:
 *  Archive Log:    Revision 1.12  2014/06/27 22:22:24  jijunwan
 *  Archive Log:    added running indicator to Performance Subpages
 *  Archive Log:
 *  Archive Log:    Revision 1.11  2014/06/05 17:34:11  jijunwan
 *  Archive Log:    added vFabric into Tree View
 *  Archive Log:
 *  Archive Log:    Revision 1.10  2014/05/18 22:51:27  rjtierne
 *  Archive Log:    Removed superfluous print statement.
 *  Archive Log:
 *  Archive Log:    Revision 1.9  2014/05/16 04:30:41  jijunwan
 *  Archive Log:    Added code to deregister from task scheduler; Added Page Listener to listen enter or exit a (sub)page
 *  Archive Log:
 *  Archive Log:    Revision 1.8  2014/05/15 14:33:16  jijunwan
 *  Archive Log:    minor change on tree controller to support generic
 *  Archive Log:
 *  Archive Log:    Revision 1.7  2014/05/09 21:13:40  jijunwan
 *  Archive Log:    minor performance tab look & feel adjustment
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2014/05/09 21:00:42  jijunwan
 *  Archive Log:    added property; fixed remembering last subpage issue; fixed position problem on IntelTabbedPane
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2014/05/02 21:53:46  rjtierne
 *  Archive Log:    Minor tweak to setTabs() to ensure that the first
 *  Archive Log:    available tab receives the focus when a node is selected
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2014/05/02 18:23:05  rjtierne
 *  Archive Log:    Restored the use of class IntelTabbedPaneUI
 *  Archive Log:    and called setFont() to tweak the appearance
 *  Archive Log:    of the subpage tabs
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/05/02 16:38:14  rjtierne
 *  Archive Log:    Updated setNodeName() to enhance the port
 *  Archive Log:    description for the node label on the tabbed pane. Also
 *  Archive Log:    added method clearPage() to display a message when
 *  Archive Log:    non-data related nodes are selected from the tree.
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/05/02 14:55:59  rjtierne
 *  Archive Log:    getMainComponent() uses IntelTabbedSubpaneUI() to
 *  Archive Log:    provide a more subtle LAF to subpage tabbed pane.
 *  Archive Log:    Also retaining tab index in setTabs() so the current
 *  Archive Log:    page remains persistent.
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/05/01 16:16:00  rjtierne
 *  Archive Log:    Ravamped to work with new design. Added Intel
 *  Archive Log:    and CVS headers
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/
package com.intel.stl.ui.monitor.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreeSelectionModel;

import com.intel.stl.ui.common.IPerfSubpageController;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.view.IntelTabbedPaneUI;
import com.intel.stl.ui.main.view.IPageListener;
import com.intel.stl.ui.monitor.TreeNodeType;
import com.intel.stl.ui.monitor.tree.FVResourceNode;

public class PerformanceTreeView extends TreeView implements IPerformanceView {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -4312903533516795863L;

    /**
     * Custom tabbed pane
     */
    private JTabbedPane tabbedPane;

    private IntelTabbedPaneUI tabUI;

    private JPanel ctrPanel;

    private final JLabel lblNodeName = new JLabel("");

    private int currentTab = 0;

    /**
     * 
     * Description: Constructor for the PerformanceView class
     * 
     */
    public PerformanceTreeView() {
        super(null, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.monitor.view.TreeView#createTree()
     */
    @Override
    protected JTree createTree() {
        JTree tree = new JTree();
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        return super.createTree();
    }

    @Override
    protected JComponent getMainComponent() {
        if (tabbedPane != null) {
            return tabbedPane;
        }

        // Create the tabbed pane which will be populated when getMainComponent
        // is called from subpages
        tabbedPane = new JTabbedPane();
        tabUI = new IntelTabbedPaneUI();
        ctrPanel = tabUI.getControlPanel();
        ctrPanel.setLayout(new BorderLayout());
        ctrPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 5));
        tabbedPane.setUI(tabUI);
        tabUI.setFont(UIConstants.H4_FONT);
        tabUI.setTabAreaInsets(new Insets(2, 5, 4, 5));
        return tabbedPane;
    }

    @Override
    public JComponent getView() {
        return this;
    }

    @Override
    public void setNodeName(FVResourceNode node) {
        String name =
                new String(node.isPort() ? node.getParent().getName() + ":"
                        + node.getName() : node.getName());
        lblNodeName.setText(name);
        lblNodeName.setForeground(UIConstants.INTEL_DARK_GRAY);
        lblNodeName.setFont(UIConstants.H5_FONT.deriveFont(Font.BOLD));
        ctrPanel.add(lblNodeName, BorderLayout.CENTER);
    }

    @Override
    public void setTabs(List<IPerfSubpageController> subpages, int selection) {
        // remove all old tabs
        // add the view of each subpage to our tabbed pane
        tabbedPane.removeAll();

        for (IPerfSubpageController subpage : subpages) {
            tabbedPane.addTab(subpage.getName(), subpage.getIcon(),
                    subpage.getView(), subpage.getDescription());
        }

        tabbedPane.setSelectedIndex(selection > 0 ? selection : 0);
    }

    public String getCurrentSubpage() {
        int currentTab = tabbedPane.getSelectedIndex();
        if (currentTab < 0) {
            return null;
        } else {
            return tabbedPane.getTitleAt(currentTab);
        }
    }

    public void clearPage(TreeNodeType nodeType) {
        String msg = new String("");

        tabbedPane.removeAll();

        switch (nodeType) {

            case INACTIVE_PORT:
                msg = UILabels.STL40004_ERROR_INACTIVE_PORT.getDescription();
                break;

            case ALL:
            case HCA_GROUP:
            case SWITCH_GROUP:
            case ROUTER_GROUP:
            case DEVICE_GROUP:
            case VIRTUAL_FABRIC:
                msg = UILabels.STL40005_TREE_INFO_MSG.getDescription();
                break;

            default:
                break;

        }
        lblNodeName.setText(msg);
        lblNodeName.setForeground(UIConstants.INTEL_BLUE);
        lblNodeName.setFont(UIConstants.H2_FONT.deriveFont(Font.PLAIN));
        getMainPanel().revalidate();
    }

    /**
     * 
     * Description:
     * 
     * @param listener
     */
    public void setPageListener(final IPageListener listener) {
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // only fire onPageChanged when we have valid oldPageId and
                // newPageId
                int oldTab = currentTab;
                currentTab = tabbedPane.getSelectedIndex();
                if (oldTab >= 0 && currentTab >= 0) {
                    listener.onPageChanged(oldTab, currentTab);
                }
            }
        });
    }

    public void setRunning(boolean isRunning) {
        if (lblNodeName != null) {
            lblNodeName.setIcon(isRunning ? UIImages.RUNNING.getImageIcon()
                    : null);
        }
    }

}