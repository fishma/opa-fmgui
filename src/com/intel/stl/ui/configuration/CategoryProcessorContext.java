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
 *  File Name: ResourceCategoryContext.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.6  2015/08/17 18:53:50  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - changed frontend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2015/07/17 15:41:39  rjtierne
 *  Archive Log:    PR 129549 - On connectivity table, clicking on cable info for an HFI results in an error
 *  Archive Log:    In an effort to make the construction of the CategoryProcessorContext more generic and
 *  Archive Log:    avert the need for an FVResourceNode:
 *  Archive Log:    	- Added new constructor for setting up a node using input parameter lid
 *  Archive Log:    	- Added new constructor for setting up a port using input parameters lid and port
 *  Archive Log:    	- Added new setupForNode() method to get a node using the input parameter lid
 *  Archive Log:    	- Added new setupForPort() method to get a port using the input parameters lid and portNum
 *  Archive Log:    	- Added new getPort() method to get a PortRecordBean from the subnet API
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2015/02/04 21:44:14  jijunwan
 *  Archive Log:    impoved to handle unsigned values
 *  Archive Log:     - we promote to a "bigger" data type
 *  Archive Log:     - port numbers are now short
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2015/01/11 21:30:25  jijunwan
 *  Archive Log:    adapt change on FM that uses port number 1 for HFI in link query
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/10/13 22:16:29  fernande
 *  Archive Log:    Fixing unit test error due to changes in TreeNodeType
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/10/13 21:04:11  fernande
 *  Archive Log:    Changed GetDevicePropertiesTask to be driven by the PropertiesDisplayOptions in UserSettings instead of hard coded
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: fernande
 *
 ******************************************************************************/

package com.intel.stl.ui.configuration;

import org.jfree.util.Log;

import com.intel.stl.api.configuration.IConfigurationApi;
import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.api.subnet.NodeInfoBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.api.subnet.PortRecordBean;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.api.subnet.SwitchInfoBean;
import com.intel.stl.api.subnet.SwitchRecordBean;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.monitor.TreeNodeType;
import com.intel.stl.ui.monitor.tree.FVResourceNode;

public class CategoryProcessorContext implements ICategoryProcessorContext {

    private FVResourceNode resourceNode;

    private final Context context;

    private NodeRecordBean node;

    private NodeInfoBean nodeInfo;

    private SwitchRecordBean switchBean;

    private SwitchInfoBean switchInfo;

    private PortRecordBean portBean;

    private PortInfoBean portInfo;

    private LinkRecordBean linkBean;

    private NodeRecordBean neighbor;

    private boolean endPort;

    public CategoryProcessorContext(FVResourceNode node, Context context) {
        this.resourceNode = node;
        this.context = context;
        try {
            switch (node.getType()) {
                case SWITCH:
                    setupForSwitch();
                    break;
                case HFI:
                    setupForNode();
                    break;
                case PORT:
                    setupForPort();
                    break;
                case ACTIVE_PORT:
                    setupForPort();
                    break;
                case INACTIVE_PORT:
                    break;
                default:
                    break;
            }
        } catch (SubnetDataNotFoundException e) {
            RuntimeException re = new RuntimeException(e.getMessage());
            re.initCause(e);
            throw re;
        }
    }

    // Constructor for a Node
    public CategoryProcessorContext(int lid, Context context) {
        this.context = context;
        try {
            setupForNode(lid);
        } catch (SubnetDataNotFoundException e) {
            RuntimeException re = new RuntimeException(e.getMessage());
            re.initCause(e);
            throw re;
        }
    }

    // Constructor for a port
    public CategoryProcessorContext(int lid, short port, Context context) {
        this.context = context;
        try {
            setupForPort(lid, port);
        } catch (SubnetDataNotFoundException e) {
            RuntimeException re = new RuntimeException(e.getMessage());
            re.initCause(e);
            throw re;
        }
    }

    @Override
    public FVResourceNode getResourceNode() {
        return resourceNode;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public ISubnetApi getSubnetApi() {
        return context.getSubnetApi();
    }

    @Override
    public IConfigurationApi getConfigurationApi() {
        return context.getConfigurationApi();
    }

    @Override
    public IPerformanceApi getPerformanceApi() {
        return context.getPerformanceApi();
    }

    @Override
    public NodeRecordBean getNode() {
        return node;
    }

    @Override
    public NodeInfoBean getNodeInfo() {
        return nodeInfo;
    }

    @Override
    public SwitchRecordBean getSwitch() {
        return switchBean;
    }

    @Override
    public SwitchInfoBean getSwitchInfo() {
        return switchInfo;
    }

    @Override
    public PortRecordBean getPort() {
        return portBean;
    }

    @Override
    public PortInfoBean getPortInfo() {
        return portInfo;
    }

    @Override
    public LinkRecordBean getLink() {
        return linkBean;
    }

    @Override
    public NodeRecordBean getNeighbor() {
        return neighbor;
    }

    @Override
    public boolean isEndPort() {
        return endPort;
    }

    private void setupForNode() throws SubnetDataNotFoundException {
        int lid = resourceNode.getId();
        node = getNode(lid);
        nodeInfo = null;
        if (node != null) {
            nodeInfo = node.getNodeInfo();
        }
    }

    protected void setupForNode(int lid) throws SubnetDataNotFoundException {
        node = getNode(lid);
        nodeInfo = null;
        if (node != null) {
            nodeInfo = node.getNodeInfo();
        }
    }

    private void setupForSwitch() throws SubnetDataNotFoundException {
        int lid = resourceNode.getId();
        node = getNode(lid);
        switchBean = getSubnetApi().getSwitch(lid);
        nodeInfo = null;
        switchInfo = null;
        if (node != null) {
            nodeInfo = node.getNodeInfo();
        }
        if (switchBean != null) {
            switchInfo = switchBean.getSwitchInfo();
        }
    }

    private void setupForPort() throws SubnetDataNotFoundException {
        FVResourceNode parent = resourceNode.getParent();
        TreeNodeType type = parent.getType();
        int lid = parent.getId();
        int portNum = resourceNode.getId();
        node = getNode(lid);
        portBean =
                type == TreeNodeType.SWITCH ? getSubnetApi().getPortByPortNum(
                        lid, (short) portNum) : getSubnetApi()
                        .getPortByLocalPortNum(lid, (short) portNum);
        nodeInfo = null;
        portInfo = null;
        linkBean = null;
        neighbor = null;
        endPort = false;
        if (node != null) {
            nodeInfo = node.getNodeInfo();
            NodeType parentType = nodeInfo.getNodeTypeEnum();
            // According to the IB spec
            // Endport: A Port which can be a destination of LID-routed
            // communication within the same Subnet as the sender. All Channel
            // Adapter ports on the subnet are endports of that subnet, as is
            // Port 0 of each Switch in the subnet. Switch ports other than Port
            // 0 may not be endports. When port is used without qualification,
            // it may be assumed to mean endport whenever the context indicates
            // that it is a destination of communication.
            if ((parentType != NodeType.SWITCH)
                    || (parentType == NodeType.SWITCH && portNum == 0)) {
                endPort = true;
            }
        }
        if (portBean != null) {
            portInfo = portBean.getPortInfo();
        }
        if (portBean != null) {
            linkBean = null;
            if (type != TreeNodeType.SWITCH) {
                linkBean = getSubnetApi().getLinkBySource(lid, (short) 1);
            } else if (portNum != 0) {
                linkBean = getSubnetApi().getLinkBySource(lid, (short) portNum);
            }
            if (linkBean != null) {
                neighbor = getNode(linkBean.getToLID());
            }
        }
    }

    protected void setupForPort(int lid, short portNum)
            throws SubnetDataNotFoundException {

        node = getNode(lid);
        portBean = getPort(lid, portNum);
        nodeInfo = null;
        portInfo = null;
        linkBean = null;
        neighbor = null;
        endPort = false;
        if (node != null) {
            nodeInfo = node.getNodeInfo();
            NodeType parentType = nodeInfo.getNodeTypeEnum();
            // According to the IB spec
            // Endport: A Port which can be a destination of LID-routed
            // communication within the same Subnet as the sender. All Channel
            // Adapter ports on the subnet are endports of that subnet, as is
            // Port 0 of each Switch in the subnet. Switch ports other than Port
            // 0 may not be endports. When port is used without qualification,
            // it may be assumed to mean endport whenever the context indicates
            // that it is a destination of communication.
            if ((parentType != NodeType.SWITCH)
                    || (parentType == NodeType.SWITCH && portNum == 0)) {
                endPort = true;
            }
        }
        if (portBean != null) {
            portInfo = portBean.getPortInfo();
        }
        if (portBean != null) {
            linkBean = null;
            if (portNum != 0) {
                linkBean = getSubnetApi().getLinkBySource(lid, portNum);
            }
            if (linkBean != null) {
                neighbor = getNode(linkBean.getToLID());
            }
        }
    }

    private NodeRecordBean getNode(int lid) throws SubnetDataNotFoundException {
        NodeRecordBean node = getSubnetApi().getNode(lid);
        return node;
    }

    protected PortRecordBean getPort(int lid, short portNum) {

        PortRecordBean portBean = null;
        try {
            portBean = getSubnetApi().getPortByPortNum(lid, portNum);
        } catch (SubnetDataNotFoundException e) {
            Log.error(e.getMessage());
        }

        return portBean;
    }
}
