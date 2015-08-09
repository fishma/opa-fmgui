/**
 * INTEL CONFIDENTIAL
 * Copyright (c) ${year} Intel Corporation All Rights Reserved.
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
 *  File Name: FVTreeBuilder.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.6  2015/02/23 22:48:44  jijunwan
 *  Archive Log:    fixed insures on tree update upon notices
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2015/02/05 21:21:44  jijunwan
 *  Archive Log:    fixed NPE issues found by klocwork
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2014/12/11 18:46:13  fernande
 *  Archive Log:    Switch from log4j to slf4j+logback
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/10/14 11:32:08  jypak
 *  Archive Log:    UI updates for notices.
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/10/09 12:36:03  fernande
 *  Archive Log:    Adding IContextAware interface to generalize context operations (setContext) and changes to the IProgressObserver interface
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/09/02 19:24:28  jijunwan
 *  Archive Log:    renamed FVTreeBuilder to tree.FVTreeManager, moved FVResourceNode and FVTreeModel  to package tree
 *  Archive Log:
 *  Archive Log:    Revision 1.30  2014/09/02 19:03:00  jijunwan
 *  Archive Log:    tree update based on merge sort algorithm
 *  Archive Log:
 *  Archive Log:    Revision 1.29  2014/08/26 14:32:05  jijunwan
 *  Archive Log:    added reset to force build tree from scratch
 *  Archive Log:
 *  Archive Log:    Revision 1.28  2014/08/15 21:46:38  jijunwan
 *  Archive Log:    adapter to the new GroupConfig and FocusPorts data structures
 *  Archive Log:
 *  Archive Log:    Revision 1.27  2014/08/12 21:06:52  jijunwan
 *  Archive Log:    add null check
 *  Archive Log:
 *  Archive Log:    Revision 1.26  2014/08/05 18:39:02  jijunwan
 *  Archive Log:    renamed FI to HFI
 *  Archive Log:
 *  Archive Log:    Revision 1.25  2014/08/05 13:36:42  jijunwan
 *  Archive Log:    fixed typo isCanceled->isCanelled, added cancel interface
 *  Archive Log:
 *  Archive Log:    Revision 1.24  2014/07/25 20:48:26  fernande
 *  Archive Log:    Cleaning up unused reference
 *  Archive Log:
 *  Archive Log:    Revision 1.23  2014/07/21 15:41:34  jijunwan
 *  Archive Log:    minor performance improvement on tree building
 *  Archive Log:
 *  Archive Log:    Revision 1.22  2014/07/07 18:18:18  jijunwan
 *  Archive Log:    improved to handle switching subnet when one is still in the process of initialization
 *  Archive Log:
 *  Archive Log:    Revision 1.21  2014/07/01 14:44:11  jijunwan
 *  Archive Log:    changed to use #hasPort from subnet api
 *  Archive Log:
 *  Archive Log:    Revision 1.20  2014/06/26 15:54:07  jijunwan
 *  Archive Log:    improvement to handle context switch
 *  Archive Log:
 *  Archive Log:    Revision 1.19  2014/06/26 15:49:10  jijunwan
 *  Archive Log:    performance improvement - share tree model among pages so we do not build model several times
 *  Archive Log:
 *  Archive Log:    Revision 1.18  2014/06/26 15:00:15  jijunwan
 *  Archive Log:    added progress indication to subnet initialization
 *  Archive Log:
 *  Archive Log:    Revision 1.17  2014/06/24 20:21:29  rjtierne
 *  Archive Log:    Changed HCA to HFI
 *  Archive Log:
 *  Archive Log:    Revision 1.16  2014/06/23 04:57:31  jijunwan
 *  Archive Log:    added null check
 *  Archive Log:
 *  Archive Log:    Revision 1.15  2014/06/19 20:13:58  fernande
 *  Archive Log:    Added background update of database and redirected some APIs to use the database.
 *  Archive Log:
 *  Archive Log:    Revision 1.14  2014/06/05 18:32:49  jijunwan
 *  Archive Log:    changed Channel Adapter to Fabric Interface
 *  Archive Log:
 *  Archive Log:    Revision 1.13  2014/06/05 17:34:10  jijunwan
 *  Archive Log:    added vFabric into Tree View
 *  Archive Log:
 *  Archive Log:    Revision 1.12  2014/06/05 12:32:45  rjtierne
 *  Archive Log:    In method buildGroupTree(), added protection to prevent null
 *  Archive Log:    access if node unavailable in nodeMap
 *  Archive Log:
 *  Archive Log:    Revision 1.11  2014/06/02 21:46:06  jijunwan
 *  Archive Log:    Fixed the issue with port number. portNum in PortInfoRecord is used only for switches. And for HFI and Router, we should use localPortNum instead.
 *  Archive Log:
 *  Archive Log:    Revision 1.10  2014/05/15 18:27:07  rjtierne
 *  Archive Log:    Tagged specific node types (ALL, HFI, SW...etc)  to nodes on the
 *  Archive Log:    device group tree instead of generic DEVICE_GROUP.  Assists in
 *  Archive Log:    obtaining the correct GroupConfig when a group is selected
 *  Archive Log:    from the tree.
 *  Archive Log:
 *  Archive Log:    Revision 1.9  2014/04/29 16:44:17  jijunwan
 *  Archive Log:    use the new NameSorter in resource tree
 *  Archive Log:
 *  Archive Log:    Revision 1.8  2014/04/28 23:24:23  rjtierne
 *  Archive Log:    Wrote new sortNodes() method to sort tree nodes
 *  Archive Log:
 *  Archive Log:    Revision 1.7  2014/04/24 21:16:37  rjtierne
 *  Archive Log:    Replaced the comparator to sort nodes on the tree
 *  Archive Log:    with a freeware alphanumeric sorting algorithm under
 *  Archive Log:    the GNU Lesser GPL
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2014/04/24 18:28:18  rjtierne
 *  Archive Log:    Renamed subnetTree to deviceTypesTree.
 *  Archive Log:    Created hash set of port beans and used to
 *  Archive Log:    set the status for inactive ports. Added
 *  Archive Log:    method headers
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2014/04/23 20:07:37  jijunwan
 *  Archive Log:    added createPort method
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2014/04/23 19:56:37  rjtierne
 *  Archive Log:    Added hash set containing all port beans
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/04/23 18:14:18  jijunwan
 *  Archive Log:    fixed a bug on tree build
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/04/23 13:45:37  jijunwan
 *  Archive Log:    improvement on TreeView
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/04/22 20:47:24  rjtierne
 *  Archive Log:    Relocated from common.view to monitor package
 *  Archive Log:
 *  Archive Log:    Revision 1.7  2014/04/22 18:31:09  jincoope
 *  Archive Log:    NodeProperties is now in model package
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2014/04/22 17:56:36  rjtierne
 *  Archive Log:    Removed creation of test tree for Virtual Fabrics and Congested Nodes
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2014/04/20 03:18:47  jijunwan
 *  Archive Log:    minor change
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2014/04/18 21:15:12  jijunwan
 *  Archive Log:    minor change on tree builder
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/04/18 16:59:48  jijunwan
 *  Archive Log:    minor adjustment
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/04/17 22:15:49  jijunwan
 *  Archive Log:    device type and device group tree build
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/04/17 14:37:27  rjtierne
 *  Archive Log:    Initial Version
 *  Archive Log:
 *
 *  Overview: This class builds trees for the channel adapters, switches, 
 *  routers, device groups, virtual fabrics, and others
 *
 *  @author: rjtierne
 *
 ******************************************************************************/
package com.intel.stl.ui.monitor.tree;

import static com.intel.stl.ui.common.PageWeight.MEDIUM;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.ui.common.IContextAware;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.ObserverAdapter;
import com.intel.stl.ui.common.PageWeight;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.monitor.TreeNodeType;
import com.intel.stl.ui.monitor.TreeTypeEnum;

public class FVTreeManager implements IContextAware {

    private static final String NAME = "TreeManager";

    Logger mLog = LoggerFactory.getLogger(FVTreeManager.class);

    SubnetDescription subnet;

    /**
     * Subnet API
     */
    ISubnetApi mSubnetApi;

    /**
     * Performance API
     */
    IPerformanceApi mPerformanceApi;

    private final EnumMap<TreeTypeEnum, TreeManagementModel> mgrModels;

    /**
     * 
     * Description: Constructor for the FVTreeBuilder class
     * 
     * @param pContext
     *            - handle to the APIs
     */
    public FVTreeManager() {
        mgrModels =
                new EnumMap<TreeTypeEnum, TreeManagementModel>(
                        TreeTypeEnum.class);
        for (TreeTypeEnum type : TreeTypeEnum.values()) {
            mgrModels.put(type, new TreeManagementModel());
        }
    }

    @Override
    public synchronized void setContext(Context pContext,
            IProgressObserver observer) {
        if (subnet == null || !pContext.getSubnetDescription().equals(subnet)) {
            mLog.info("Clear trees because subnet changed from " + subnet
                    + " to " + pContext.getSubnetDescription());
            subnet = pContext.getSubnetDescription();
            mSubnetApi = pContext.getSubnetApi();
            mPerformanceApi = pContext.getPerformanceApi();
            reset();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * 
     * Description: Builds the type of tree specified by the input parameter
     * 
     * @param pTreeType
     *            - type of tree to build
     * 
     * @return root node of the tree
     */
    public synchronized FVResourceNode buildTree(TreeTypeEnum pTreeType,
            IProgressObserver observer) {
        long t = System.currentTimeMillis();
        if (observer == null) {
            observer = new ObserverAdapter();
        }

        // observer.setNote(pTreeType.getName());
        FVResourceNode node = null;

        switch (pTreeType) {
            case DEVICE_TYPES_TREE:
                node = createDeviceTypesTree(observer);
                break;

            case DEVICE_GROUPS_TREE:
                node = createDeviceGroupsTree(observer);
                break;

            case VIRTUAL_FABRICS_TREE:
                node = createVFsTree(observer);
                break;

            case TOP_10_CONGESTED_TREE:
                observer.onFinish();
                break;

            default:
                break;
        } // switch

        mLog.info("Build tree " + pTreeType + " in "
                + (System.currentTimeMillis() - t) + " ms");
        return node;
    }

    public synchronized void updateTree(TreeTypeEnum pTreeType,
            IProgressObserver observer) {
        long t = System.currentTimeMillis();
        if (observer == null) {
            observer = new ObserverAdapter();
        }

        // observer.setNote(pTreeType.getName());
        switch (pTreeType) {
            case DEVICE_TYPES_TREE:
                updateDeviceTypesTree(observer);
                break;

            case DEVICE_GROUPS_TREE:
                updateDeviceGroupsTree(observer);
                break;

            case VIRTUAL_FABRICS_TREE:
                updateVFsTree(observer);
                break;

            case TOP_10_CONGESTED_TREE:
                observer.onFinish();
                break;

            default:
                break;
        } // switch

        mLog.info("Update tree " + pTreeType + " in "
                + (System.currentTimeMillis() - t) + " ms");
    }

    public synchronized void updateTreeNode(int lid, TreeTypeEnum pTreeType) {
        long t = System.currentTimeMillis();

        switch (pTreeType) {
            case DEVICE_TYPES_TREE:
                updateDeviceTypesTreeNode(lid);
                break;

            case DEVICE_GROUPS_TREE:
                updateDeviceGroupsTreeNode(lid);
                break;

            case VIRTUAL_FABRICS_TREE:
                updateVFsTreeNode(lid);
                break;

            case TOP_10_CONGESTED_TREE:
                break;

            default:
                break;
        } // switch

        mLog.info("Update tree node" + pTreeType + " in "
                + (System.currentTimeMillis() - t) + " ms");
    }

    public void addMonitor(TreeTypeEnum treeType, ITreeMonitor monitor) {
        TreeManagementModel model = mgrModels.get(treeType);
        if (model != null) {
            model.addTreeMonitor(monitor);
        } else {
            throw new IllegalArgumentException(
                    "Couldn't find TreeManagementModel for " + treeType);
        }
    }

    public void removeMonitor(TreeTypeEnum treeType, ITreeMonitor monitor) {
        TreeManagementModel model = mgrModels.get(treeType);
        if (model != null) {
            model.removeTreeMonitor(monitor);
        } else {
            throw new IllegalArgumentException(
                    "Couldn't find TreeManagementModel for " + treeType);
        }
    }

    /**
     * 
     * Description: reset this builder so the cached <code>subnet</code> will be
     * created from scratch. call this method when we switch to another subnet
     * or current subnet is changed.
     * 
     */
    public synchronized void reset() {
        for (TreeManagementModel model : mgrModels.values()) {
            model.reset();
        }
    }

    public synchronized void setDirty() {
        for (TreeManagementModel model : mgrModels.values()) {
            model.setDirty(true);
        }
    }

    /**
     * 
     * Description: Creates the device types tree consisting of channel
     * adapters, switches, and routers
     * 
     * @return rootNode - root node of the device types tree
     */
    protected FVResourceNode createDeviceTypesTree(IProgressObserver observer) {
        if (observer == null) {
            observer = new ObserverAdapter();
        }

        TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.DEVICE_TYPES_TREE);
        if (model.isValid()) {
            observer.onFinish();
            return model.getTree();
        }

        // Create the root node of the tree
        FVResourceNode deviceTypesTree =
                new FVResourceNode(subnet.getName(), TreeNodeType.ALL,
                        TreeNodeType.ALL.ordinal());

        DeviceTypesTreeSynchronizer treeUpdater =
                new DeviceTypesTreeSynchronizer(mSubnetApi);
        treeUpdater.updateTree(deviceTypesTree, null, observer);
        model.setTree(deviceTypesTree);
        return deviceTypesTree;
    }

    /**
     * 
     * <i>Description:</i> update device types tree
     * 
     * @param observer
     *            progress observer used to notify update progress
     * @param model
     *            TreeModel used to fire tree change events when necessary
     */
    protected void updateDeviceTypesTree(final IProgressObserver observer) {
        final TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.DEVICE_TYPES_TREE);
        if (model.isEmpty()) {
            createDeviceTypesTree(observer);
            return;
        }

        if (model.isDirty()) {
            final DeviceTypesTreeSynchronizer treeUpdater =
                    new DeviceTypesTreeSynchronizer(mSubnetApi);
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        treeUpdater.updateTree(model.getTree(),
                                model.getMonitors(), observer);
                        model.setDirty(false);
                    }
                });
            } catch (InterruptedException e) {
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if (observer != null) {
            observer.onFinish();
        }
    }

    protected void updateDeviceTypesTreeNode(final int lid) {
        final TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.DEVICE_TYPES_TREE);
        if (model.isEmpty()) {
            return;
        } else {
            // Don't need to set model dirty so don't check if it's dirty.
            final DeviceTypesTreeUpdater treeUpdater =
                    new DeviceTypesTreeUpdater(mSubnetApi);
            try {
                // This will make Runnable run in EDT and block until it return.
                // To set the progress correctly, it should block.
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        FVResourceNode tree = model.getTree();
                        List<ITreeMonitor> monitors = model.getMonitors();
                        treeUpdater.updateNode(lid, tree, monitors);
                    }
                });
            } catch (InterruptedException e) {
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * Description: Creates the device groups tree consisting of groups of
     * network resources
     * 
     * @return root node of the device groups tree
     */
    protected FVResourceNode createDeviceGroupsTree(IProgressObserver observer) {
        if (observer == null) {
            observer = new ObserverAdapter();
        }

        TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.DEVICE_GROUPS_TREE);
        if (model.isValid()) {
            return model.getTree();
        }
        IProgressObserver[] subObservers = observer.createSubObservers(2);

        FVResourceNode deviceGroupsTree =
                new FVResourceNode(subnet.getName(), TreeNodeType.ALL,
                        TreeNodeType.ALL.ordinal());
        FVResourceNode subnetTree = createDeviceTypesTree(subObservers[0]);
        subObservers[0].onFinish();
        DeviceGroupsTreeSynchronizer treeUpdater =
                new DeviceGroupsTreeSynchronizer(mPerformanceApi, subnetTree);
        treeUpdater.updateTree(deviceGroupsTree, null, subObservers[1]);
        subObservers[1].onFinish();
        model.setTree(deviceGroupsTree);
        return deviceGroupsTree;
    }

    protected void updateDeviceGroupsTree(IProgressObserver observer) {
        final TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.DEVICE_GROUPS_TREE);

        if (model.isEmpty()) {
            createDeviceGroupsTree(observer);
            return;
        }

        if (model.isDirty()) {
            if (observer == null) {
                observer = new ObserverAdapter();
            }
            final IProgressObserver[] subObservers =
                    observer.createSubObservers(2);

            FVResourceNode subnetTree = createDeviceTypesTree(subObservers[0]);
            subObservers[0].onFinish();
            final DeviceGroupsTreeSynchronizer treeUpdater =
                    new DeviceGroupsTreeSynchronizer(mPerformanceApi,
                            subnetTree);
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        treeUpdater.updateTree(model.getTree(),
                                model.getMonitors(), subObservers[1]);
                        subObservers[1].onFinish();
                        model.setDirty(false);
                    }
                });
            } catch (InterruptedException e) {
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if (observer != null) {
            observer.onFinish();
        }
    }

    protected void updateDeviceGroupsTreeNode(final int lid) {
        final TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.DEVICE_GROUPS_TREE);

        if (model.isEmpty()) {
            return;
        } else {
            final DeviceGroupsTreeUpdater treeUpdater =
                    new DeviceGroupsTreeUpdater(mSubnetApi, mPerformanceApi);
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        FVResourceNode tree = model.getTree();
                        List<ITreeMonitor> monitors = model.getMonitors();
                        treeUpdater.updateNode(lid, tree, monitors);
                    }
                });
            } catch (InterruptedException e) {
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * Description: Creates the virtual fabric tree consisting of vFabrics of
     * network resources
     * 
     * @return root node of the virtual fabric tree
     */
    protected FVResourceNode createVFsTree(IProgressObserver observer) {
        if (observer == null) {
            observer = new ObserverAdapter();
        }

        TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.VIRTUAL_FABRICS_TREE);
        if (model.isValid()) {
            observer.onFinish();
            return model.getTree();
        }

        IProgressObserver[] subObsevers = observer.createSubObservers(2);

        FVResourceNode vfTree =
                new FVResourceNode(subnet.getName(), TreeNodeType.ALL,
                        TreeNodeType.ALL.ordinal());
        FVResourceNode subnetTree = createDeviceTypesTree(subObsevers[0]);
        subObsevers[0].onFinish();
        VirtualFabricsTreeSynchronizer treeUpdater =
                new VirtualFabricsTreeSynchronizer(mPerformanceApi, subnetTree);
        treeUpdater.updateTree(vfTree, null, subObsevers[1]);
        subObsevers[1].onFinish();
        model.setTree(vfTree);
        return vfTree;
    }

    protected void updateVFsTree(IProgressObserver observer) {
        final TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.VIRTUAL_FABRICS_TREE);
        if (model.isEmpty()) {
            createVFsTree(observer);
            return;
        }

        if (model.isDirty()) {
            if (observer == null) {
                observer = new ObserverAdapter();
            }
            final IProgressObserver[] subObservers =
                    observer.createSubObservers(2);

            FVResourceNode subnetTree = createDeviceTypesTree(subObservers[0]);
            subObservers[0].onFinish();
            final VirtualFabricsTreeSynchronizer treeUpdater =
                    new VirtualFabricsTreeSynchronizer(mPerformanceApi,
                            subnetTree);
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        treeUpdater.updateTree(model.getTree(),
                                model.getMonitors(), subObservers[1]);
                        subObservers[1].onFinish();
                        model.setDirty(false);
                    }
                });
            } catch (InterruptedException e) {
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if (observer != null) {
            observer.onFinish();
        }
    }

    protected void updateVFsTreeNode(final int lid) {
        final TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.VIRTUAL_FABRICS_TREE);
        if (model.isEmpty()) {
            return;
        } else {
            final VirtualFabricsTreeUpdater treeUpdater =
                    new VirtualFabricsTreeUpdater(mSubnetApi, mPerformanceApi);
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        treeUpdater.updateNode(lid, model.getTree(),
                                model.getMonitors());
                    }
                });
            } catch (InterruptedException e) {
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    class TreeManagementModel {
        private FVResourceNode tree;

        private boolean isDirty = true;

        private final List<ITreeMonitor> monitors =
                new ArrayList<ITreeMonitor>();

        /**
         * @return the tree
         */
        public FVResourceNode getTree() {
            return tree;
        }

        /**
         * @param tree
         *            the tree to set
         */
        public void setTree(FVResourceNode tree) {
            this.tree = tree;
            isDirty = false;
        }

        /**
         * @return the isDirty
         */
        public boolean isDirty() {
            return isDirty;
        }

        /**
         * @param isDirty
         *            the isDirty to set
         */
        public void setDirty(boolean isDirty) {
            this.isDirty = isDirty;
        }

        public void addTreeMonitor(ITreeMonitor monitor) {
            monitors.add(monitor);
        }

        public void removeTreeMonitor(ITreeMonitor monitor) {
            monitors.remove(monitor);
        }

        /**
         * @return the monitors
         */
        public List<ITreeMonitor> getMonitors() {
            return monitors;
        }

        public boolean isValid() {
            return tree != null && !isDirty;
        }

        public boolean isEmpty() {
            return tree == null;
        }

        public void reset() {
            tree = null;
            isDirty = true;
        }

    }

    @Override
    public PageWeight getContextSwitchWeight() {
        return MEDIUM;
    }

    @Override
    public PageWeight getRefreshWeight() {
        return MEDIUM;
    }
} // FVTreeBuilder