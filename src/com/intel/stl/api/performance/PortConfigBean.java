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
 *  File Name: PortConfigBean.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.5  2015/08/17 18:48:41  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - change backend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2015/02/04 21:37:53  jijunwan
 *  Archive Log:    impoved to handle unsigned values
 *  Archive Log:     - we promote to a "bigger" data type
 *  Archive Log:     - port numbers are now short
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/08/12 20:07:44  jijunwan
 *  Archive Log:    1) renamed HexUtils to StringUtils
 *  Archive Log:    2) added a method to StringUtils to get error message for an exception
 *  Archive Log:    3) changed all code to call StringUtils to get error message
 *  Archive Log:    4) some extra ode format change
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/06/03 20:49:31  jijunwan
 *  Archive Log:    added VF related PA attributes: GetVFList, GetVFInfo, GetVFConfig, GetVFPortCounters and GetVFFocusPorts
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/04/12 19:23:05  fernande
 *  Archive Log:    Initial version
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/04/01 21:37:06  jijunwan
 *  Archive Log:    Added PA attributes GroupConfig
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.api.performance;

import java.io.Serializable;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.Utils;

public class PortConfigBean implements Serializable {
    private static final long serialVersionUID = -215247442438927532L;

    private long nodeGUID;

    private String nodeDesc;

    private int nodeLid;

    private short portNumber; // promote to handle unsigned byte

    public PortConfigBean() {
    }

    public PortConfigBean(long nodeGUID, String nodeDesc, int nodeLid,
            short portNumber) {
        super();
        this.nodeGUID = nodeGUID;
        this.nodeDesc = nodeDesc;
        this.nodeLid = nodeLid;
        this.portNumber = portNumber;
    }

    /**
     * @return the nodeGUID
     */
    public long getNodeGUID() {
        return nodeGUID;
    }

    /**
     * @param nodeGUID
     *            the nodeGUID to set
     */
    public void setNodeGUID(long nodeGUID) {
        this.nodeGUID = nodeGUID;
    }

    /**
     * @return the nodeDesc
     */
    public String getNodeDesc() {
        return nodeDesc;
    }

    /**
     * @param nodeDesc
     *            the nodeDesc to set
     */
    public void setNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc;
    }

    /**
     * @return the nodeLid
     */
    public int getNodeLid() {
        return nodeLid;
    }

    /**
     * @param nodeLid
     *            the nodeLid to set
     */
    public void setNodeLid(int nodeLid) {
        this.nodeLid = nodeLid;
    }

    /**
     * @return the portNumber
     */
    public short getPortNumber() {
        return portNumber;
    }

    /**
     * @param portNumber
     *            the portNumber to set
     */
    public void setPortNumber(short portNumber) {
        this.portNumber = portNumber;
    }

    /**
     * @param portNumber
     *            the portNumber to set
     */
    public void setPortNumber(byte portNumber) {
        this.portNumber = Utils.unsignedByte(portNumber);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PortConfigBean [nodeGUID="
                + StringUtils.longHexString(nodeGUID) + ", nodeDesc="
                + nodeDesc + ", nodeLid=" + StringUtils.intHexString(nodeLid)
                + ", portNumber=" + portNumber + "]";
    }

}
