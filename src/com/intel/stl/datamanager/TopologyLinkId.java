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
 *  File Name: TopologyLink.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.6  2015/08/17 18:49:14  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - change backend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2015/02/04 21:37:57  jijunwan
 *  Archive Log:    impoved to handle unsigned values
 *  Archive Log:     - we promote to a "bigger" data type
 *  Archive Log:     - port numbers are now short
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2014/09/30 15:37:20  fernande
 *  Archive Log:    Changed hashCode methods to use generated code by Eclipse
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/08/26 15:24:31  jijunwan
 *  Archive Log:    changed port index to be byte to be consistent with port number
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/08/26 15:01:18  jijunwan
 *  Archive Log:    changed port index to be byte to be consistent with port number
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/06/11 22:08:02  fernande
 *  Archive Log:    Changes to add more entities to database schema
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: fernande
 *
 ******************************************************************************/

package com.intel.stl.datamanager;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TopologyLinkId implements Serializable {

    private static final long serialVersionUID = 1L;

    // The following fields names have been selected so that the ascending
    // alphabetical order yields the desired key for the table: topologyId +
    // nodeGUID + port
    // This is due to a limitation in Hibernate
    @Column(name = "topologyId")
    private long linkTopology;

    @Column(name = "fromNodeGUID")
    private long sourceNode;

    @Column(name = "fromPort")
    private short sourcePort;

    @Column(name = "toNodeGUID")
    private long targetNode;

    @Column(name = "toPort")
    private short targetPort;

    public long getLinkTopology() {
        return linkTopology;
    }

    public void setLinkTopology(long topologyId) {
        this.linkTopology = topologyId;
    }

    public long getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(long sourceNodeGUID) {
        this.sourceNode = sourceNodeGUID;
    }

    public short getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(short sourcePort) {
        this.sourcePort = sourcePort;
    }

    public long getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(long targetNodeGUID) {
        this.targetNode = targetNodeGUID;
    }

    public short getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(short targetPort) {
        this.targetPort = targetPort;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (linkTopology ^ (linkTopology >>> 32));
        result = prime * result + (int) (sourceNode ^ (sourceNode >>> 32));
        result = prime * result + sourcePort;
        result = prime * result + (int) (targetNode ^ (targetNode >>> 32));
        result = prime * result + targetPort;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TopologyLinkId other = (TopologyLinkId) obj;
        if (linkTopology != other.linkTopology) {
            return false;
        }
        if (sourceNode != other.sourceNode) {
            return false;
        }
        if (sourcePort != other.sourcePort) {
            return false;
        }
        if (targetNode != other.targetNode) {
            return false;
        }
        if (targetPort != other.targetPort) {
            return false;
        }
        return true;
    }
}
