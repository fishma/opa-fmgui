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
 *  File Name: GroupConfigRecord.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.7  2015/08/17 18:49:14  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - change backend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2015/02/06 15:03:04  fernande
 *  Archive Log:    Database modifications to use a long as the id for a SubnetDescription and to support users per subnet.
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2014/09/30 15:37:20  fernande
 *  Archive Log:    Changed hashCode methods to use generated code by Eclipse
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2014/09/29 20:09:30  fernande
 *  Archive Log:    Fixing potential Hibernate issue where equals returns NullPointerException. Added test to make sure it works for all database records.
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/08/15 21:38:05  jijunwan
 *  Archive Log:    1) implemented the new GroupConfig and FocusPorts queries that use separated req and rsp data structure
 *  Archive Log:    2) adapter our drive and db code to the new data structure
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/06/20 16:56:12  fernande
 *  Archive Log:    Added basic Entity Manager management to minimize creation of DAOs
 *  Archive Log:    Fixed bugs in database management
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

import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "GROUPS")
public class GroupConfigRecord extends DatabaseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private GroupConfigId id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "subnetId", insertable = false, updatable = false)
    private SubnetRecord subnet;

    @OneToMany(fetch = LAZY, mappedBy = "groupConfig")
    private List<PortConfigRecord> ports;

    @OneToMany(fetch = LAZY, mappedBy = "groupConfig")
    private List<GroupInfoRecord> groupInfos;

    public SubnetRecord getSubnet() {
        return subnet;
    }

    public void setSubnet(SubnetRecord subnet) {
        this.subnet = subnet;
    }

    public GroupConfigId getId() {
        return id;
    }

    public void setId(GroupConfigId id) {
        this.id = id;
    }

    public List<PortConfigRecord> getPorts() {
        return ports;
    }

    public void setPorts(List<PortConfigRecord> ports) {
        this.ports = ports;
    }

    public List<GroupInfoRecord> getGroupInfos() {
        return groupInfos;
    }

    public void setGroupInfos(List<GroupInfoRecord> groupInfos) {
        this.groupInfos = groupInfos;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GroupConfigRecord other = (GroupConfigRecord) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
