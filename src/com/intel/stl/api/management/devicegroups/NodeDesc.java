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
 *  File Name: NodeDesc.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.3  2015/08/17 18:49:08  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - change backend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2015/03/27 15:43:25  jijunwan
 *  Archive Log:    improvement on #copy for Application, DeviceGroup and VirtualFabric
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/03/24 17:33:19  jijunwan
 *  Archive Log:    introduced IAttribute for attributes defined in xml file
 *  Archive Log:    changed all attributes for Appliation and DG to be an IAttribute
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.api.management.devicegroups;

import com.intel.stl.api.management.StringNode;
import com.intel.stl.api.management.XMLConstants;

public class NodeDesc extends StringNode {
    private static final long serialVersionUID = -2041449579773690145L;

    public NodeDesc() {
        this(null);
    }

    /**
     * Description:
     * 
     * @param desc
     */
    public NodeDesc(String desc) {
        super(XMLConstants.NODE_DESC, desc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.StringNode#installDevieGroup(com.intel.stl
     * .api.management.devicegroups.DeviceGroup)
     */
    @Override
    public void installDevieGroup(DeviceGroup group) {
        group.addNodeDesc(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.management.StringNode#copy()
     */
    @Override
    public NodeDesc copy() {
        return new NodeDesc(value);
    }

}
