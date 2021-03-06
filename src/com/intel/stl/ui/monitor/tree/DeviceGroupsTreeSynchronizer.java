/**
 * Copyright (c) 2015, Intel Corporation
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Intel Corporation nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*******************************************************************************
 *                       I N T E L   C O R P O R A T I O N
 *	
 *  Functional Group: Fabric Viewer Application
 *
 *  File Name: DeviceGroupTreeUpdater.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.10  2015/09/30 13:26:45  fisherma
 *  Archive Log:    PR 129357 - ability to hide inactive ports.  Also fixes PR 129689 - Connectivity table exhibits inconsistent behavior on Performance and Topology pages
 *  Archive Log:
 *  Archive Log:    Revision 1.9  2015/08/17 18:54:19  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - changed frontend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.8  2015/04/16 19:21:45  jijunwan
 *  Archive Log:    removed work around code for bug in FM 390
 *  Archive Log:
 *  Archive Log:    Revision 1.7  2015/04/09 16:09:53  jijunwan
 *  Archive Log:    workaround for PR 128038
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2015/04/09 03:33:41  jijunwan
 *  Archive Log:    updated to match FM 390
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2015/02/05 21:21:44  jijunwan
 *  Archive Log:    fixed NPE issues found by klocwork
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2015/01/29 21:30:44  jijunwan
 *  Archive Log:    handle empty group
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/09/03 20:38:44  jijunwan
 *  Archive Log:    minor improvement on tree synchronizer
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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.intel.stl.api.performance.GroupConfigRspBean;
import com.intel.stl.api.performance.GroupListBean;
import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.ui.common.IProgressObserver;

public class DeviceGroupsTreeSynchronizer extends TreeSynchronizer<String> {
    private final IPerformanceApi perfApi;

    /**
     * we use DeviceTypesTree as a reference tree to help us quickly create
     * device nodes
     */
    private final FVResourceNode deviceTypesTree;

    private Map<String, Integer> groups;

    private Map<Integer, FVResourceNode> nodeMap;

    private final CopyBasedNodesSynchronizer nodesUpdater;

    private final Comparator<Integer> comparator = new Comparator<Integer>() {

        @Override
        public int compare(Integer o1, Integer o2) {
            FVResourceNode node = nodeMap.get(o1);
            String name1 = node == null ? null : node.getName();
            node = nodeMap.get(o2);
            String name2 = node == null ? null : node.getName();
            return TreeNodeFactory.comapreNodeName(name1, name2);
        }

    };

    public DeviceGroupsTreeSynchronizer(IPerformanceApi perfApi,
            FVResourceNode deviceTypesTree) {
        this(perfApi, deviceTypesTree, true);
    }

    /**
     * Description:
     * 
     * @param nodeComparator
     * @param subnetApi
     * @param deviceTypesTree
     */
    public DeviceGroupsTreeSynchronizer(IPerformanceApi perfApi,
            FVResourceNode deviceTypesTree, boolean removeEmptyGroup) {
        super(removeEmptyGroup);
        this.perfApi = perfApi;
        this.deviceTypesTree = deviceTypesTree;
        initData();
        nodesUpdater = new CopyBasedNodesSynchronizer(nodeMap);
    }

    protected void initData() {
        List<GroupListBean> groupList = perfApi.getGroupList();
        groups = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < groupList.size(); i++) {
            groups.put(groupList.get(i).getGroupName(), i);
        }

        nodeMap = new HashMap<Integer, FVResourceNode>();
        for (FVResourceNode type : deviceTypesTree.getChildren()) {
            for (FVResourceNode node : type.getChildren()) {
                nodeMap.put(node.getId(), node);
            }
        }
    }

    public void updateTree(FVResourceNode parent, List<ITreeMonitor> monitors,
            IProgressObserver observer) {
        updateTree(parent, groups.keySet().toArray(new String[0]), monitors,
                observer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.monitor.tree.FastTreeUpater#compare(com.intel.stl.ui
     * .monitor.FVResourceNode, java.lang.Object)
     */
    @Override
    protected int compare(FVResourceNode node, String element) {
        return TreeNodeFactory.compareNameByIndex(node.getName(), element,
                groups);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.monitor.tree.TreeUpater#createNode(java.lang.Object)
     */
    @Override
    protected FVResourceNode createNode(String key) {
        Integer id = groups.get(key);
        if (id != null) {
            return TreeNodeFactory.createGroupNode(key, id);
        } else {
            throw new IllegalArgumentException("Couldn't find Device Group '"
                    + key + "'");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.monitor.tree.TreeUpater#addNode(int,
     * com.intel.stl.ui.monitor.FVResourceNode,
     * com.intel.stl.ui.monitor.FVTreeModel)
     */
    @Override
    protected FVResourceNode addNode(int index, String key,
            FVResourceNode parent, List<ITreeMonitor> monitors,
            IProgressObserver observer) {
        FVResourceNode node =
                super.addNode(index, key, parent, monitors, observer);
        // we call updateNode to fill children for a device group node
        updateNode(node, parent, monitors, observer);
        return node;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.monitor.tree.TreeUpater#updateNode(com.intel.stl.ui.
     * monitor.FVResourceNode, com.intel.stl.ui.monitor.FVResourceNode,
     * com.intel.stl.ui.monitor.FVTreeModel,
     * com.intel.stl.ui.common.IProgressObserver)
     */
    @Override
    protected void updateNode(FVResourceNode node, FVResourceNode parent,
            List<ITreeMonitor> monitors, IProgressObserver observer) {
        List<GroupConfigRspBean> groupConfig =
                perfApi.getGroupConfig(node.getName());
        if (groupConfig == null) {
            return;
        }

        Set<Integer> elements = new HashSet<Integer>();
        for (GroupConfigRspBean bean : groupConfig) {
            int lid = bean.getPort().getNodeLid();
            if (!elements.contains(lid)) {
                elements.add(lid);
            }
        }

        if (!elements.isEmpty()) {
            // we assume a device group is organized at node level, i.e. all
            // ports belong to a node will be in a device group. If in the
            // future, we extend device group to port group, then we need to
            // change our logic and also tree structure here. We may turn out
            // have a node tree with partial ports. Right now, a node always has
            // all ports and some are marked as inactive.
            Integer[] members = elements.toArray(new Integer[0]);
            Arrays.sort(members, comparator);
            nodesUpdater.updateTree(node, members, monitors, observer);
        } else {
            FVResourceNode[] children =
                    new FVResourceNode[node.getModelChildCount()];
            int[] childIndex = new int[node.getModelChildCount()];
            int index = 0;
            while (node.getModelChildCount() > 0) {
                childIndex[index] = index;
                children[index++] = node.removeChild(0);
            }
            if (monitors != null) {
                for (ITreeMonitor monitor : monitors) {
                    monitor.fireTreeNodesRemoved(this,
                            node.getPath().getPath(), childIndex, children);
                }
            }
        }
    }
}
