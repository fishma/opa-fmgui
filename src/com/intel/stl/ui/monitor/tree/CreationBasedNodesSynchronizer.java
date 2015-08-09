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
 *  File Name: DeviceTypeNodesUpdater.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.6  2015/02/05 21:21:44  jijunwan
 *  Archive Log:    fixed NPE issues found by klocwork
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2015/02/04 21:44:20  jijunwan
 *  Archive Log:    impoved to handle unsigned values
 *  Archive Log:     - we promote to a "bigger" data type
 *  Archive Log:     - port numbers are now short
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2014/10/09 21:24:52  jijunwan
 *  Archive Log:    improvement on TreeNodeType:
 *  Archive Log:    1) Added icon to TreeNodeType
 *  Archive Log:    2) Rename PORT to ACTIVE_PORT
 *  Archive Log:    3) Removed NODE
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/09/03 18:10:26  jijunwan
 *  Archive Log:    new Tree Updaters
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/09/02 19:24:28  jijunwan
 *  Archive Log:    renamed FVTreeBuilder to tree.FVTreeManager, moved FVResourceNode and FVTreeModel  to package tree
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/09/02 19:02:59  jijunwan
 *  Archive Log:    tree update based on merge sort algorithm
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.ui.monitor.tree;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.monitor.TreeNodeType;

public class CreationBasedNodesSynchronizer extends TreeSynchronizer<Integer> {
    private static final Logger log = LoggerFactory
            .getLogger(CreationBasedNodesSynchronizer.class);

    private final ISubnetApi subnetApi;

    private final Map<Integer, NodeRecordBean> nodeMap;

    /**
     * Description:
     * 
     * @param nodeComparator
     * @param nodeMap
     */
    public CreationBasedNodesSynchronizer(ISubnetApi subnetApi,
            Map<Integer, NodeRecordBean> nodeMap) {
        super(false);
        this.subnetApi = subnetApi;
        this.nodeMap = nodeMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.monitor.tree.FastTreeUpater#compare(com.intel.stl.ui
     * .monitor.FVResourceNode, java.lang.Object)
     */
    @Override
    protected int compare(FVResourceNode node, Integer element) {
        String name1 = node.getName();
        NodeRecordBean node2 = nodeMap.get(element);
        String name2 = node2 == null ? null : node2.getNodeDesc();
        return TreeNodeFactory.comapreNodeName(name1, name2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.monitor.tree.TreeUpater#createNode(int)
     */
    @Override
    protected FVResourceNode createNode(Integer id) {
        NodeRecordBean bean = nodeMap.get(id);
        return bean == null ? new FVResourceNode("null", null, -1)
                : TreeNodeFactory.createNode(bean);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.monitor.tree.TreeUpater#addNode(int,
     * com.intel.stl.ui.monitor.FVResourceNode,
     * com.intel.stl.ui.monitor.FVTreeModel)
     */
    @Override
    protected FVResourceNode addNode(int index, Integer id,
            FVResourceNode parent, List<ITreeMonitor> monitors,
            IProgressObserver observer) {
        FVResourceNode node =
                super.addNode(index, id, parent, monitors, observer);
        // we call updateNode to fill ports for a device node
        updateNode(node, parent, monitors, observer);
        return node;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.monitor.tree.TreeUpater#updateNode(com.intel.stl.ui.
     * monitor.FVResourceNode, com.intel.stl.ui.monitor.FVResourceNode,
     * com.intel.stl.ui.monitor.FVTreeModel)
     */
    @Override
    protected void updateNode(FVResourceNode node, FVResourceNode parent,
            List<ITreeMonitor> monitors, IProgressObserver observer) {
        boolean hasChanged = false;
        NodeRecordBean bean = nodeMap.get(node.getId());
        if (bean == null) {
            log.warn("Couldn't update tree because no node " + node.getId()
                    + " found");
            return;
        }

        int numPorts = bean.getNodeInfo().getNumPorts();
        if (node.getType() == TreeNodeType.SWITCH) {
            numPorts += 1; // count in internal port
        }
        int toUpdate = Math.min(numPorts, node.getChildCount());
        for (int i = 0; i < toUpdate; i++) {
            // update ports
            FVResourceNode port = node.getChildAt(i);
            boolean statusChanged = setPortStatus(node, port);
            if (statusChanged && !hasChanged) {
                hasChanged = true;
            }
        }
        if (toUpdate < node.getChildCount()) {
            // remove ports
            while (node.getChildCount() > toUpdate) {
                node.removeChild(toUpdate);
            }
            if (!hasChanged) {
                hasChanged = true;
            }
        } else if (toUpdate < numPorts) {
            // add ports
            if (node.getType() == TreeNodeType.SWITCH) {
                numPorts -= 1;
                toUpdate -= 1;
            }
            for (int i = toUpdate + 1; i <= numPorts; i++) {
                FVResourceNode port =
                        new FVResourceNode(Integer.toString(i),
                                TreeNodeType.ACTIVE_PORT, i);

                // If the port is in the hash set, make it active
                // otherwise make it inactive
                setPortStatus(node, port);
                node.addChild(port);
            }
            if (!hasChanged) {
                hasChanged = true;
            }
        }
        if (hasChanged && monitors != null) {
            fireNodesUpdated(monitors, node);
        }
    }

    private boolean setPortStatus(FVResourceNode parentNode,
            FVResourceNode portNode) {
        int lid = parentNode.getId();
        int portNum = portNode.getId();
        boolean isActive = false;
        if (parentNode.getType() == TreeNodeType.SWITCH) {
            isActive = subnetApi.hasPort(lid, (short) portNum);
        } else {
            assert portNum > 0 : "HFI(" + parentNode
                    + ") has invalid local port number " + portNum;
            isActive = subnetApi.hasLocalPort(lid, (short) portNum);
        }

        if (isActive) {
            if (portNode.getType() != TreeNodeType.ACTIVE_PORT) {
                portNode.setType(TreeNodeType.ACTIVE_PORT);
                return true;
            }
        } else {
            if (portNode.getType() != TreeNodeType.INACTIVE_PORT) {
                portNode.setType(TreeNodeType.INACTIVE_PORT);
                return true;
            }
        }
        return false;
    }

}