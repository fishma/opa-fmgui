/**
 * INTEL CONFIDENTIAL
 * Copyright (c) 2015 Intel Corporation All Rights Reserved.
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
 *  File Name: DeviceNode.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.1  2015/03/24 17:46:10  jijunwan
 *  Archive Log:    init version of DeviceGroup editor
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.ui.admin.impl.devicegroups;

import java.util.Vector;

import com.intel.stl.ui.monitor.TreeNodeType;
import com.intel.stl.ui.monitor.tree.FVResourceNode;

public class DeviceNode extends FVResourceNode {
    private final long guid;

    private int selectCount;

    /**
     * Description:
     * 
     * @param pTitle
     * @param pType
     * @param id
     */
    public DeviceNode(String pTitle, TreeNodeType pType, int id, long guid) {
        super(pTitle, pType, id);
        this.guid = guid;
    }

    /**
     * @return the guid
     */
    public long getGuid() {
        return guid;
    }

    /**
     * @return the isSelected
     */
    public boolean isSelected() {
        return selectCount > 0;
    }

    /**
     * @param isSelected
     *            the isSelected to set
     */
    public void setSelected(boolean isSelected) {
        if (isSelected) {
            selectCount += 1;
        } else {
            selectCount -= 1;
        }
        Vector<FVResourceNode> children = getChildren();
        for (FVResourceNode child : children) {
            ((DeviceNode) child).setSelected(isSelected);
        }
    }

    public void clearSelection() {
        selectCount = 0;
        Vector<FVResourceNode> children = getChildren();
        for (FVResourceNode child : children) {
            ((DeviceNode) child).clearSelection();
        }
    }
}