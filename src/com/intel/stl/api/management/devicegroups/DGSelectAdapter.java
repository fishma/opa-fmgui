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
 *  File Name: DGSelectAdapter.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.1  2015/03/16 22:00:58  jijunwan
 *  Archive Log:    changed package name from application to applications, and from devicegroup to devicegroups
 *  Archive Log:    Added #getType to ServiceID, MGID, LongNode and their subclasses,
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/03/13 20:56:58  jijunwan
 *  Archive Log:    minor  improvement on FM Application
 *  Archive Log:    Added support on FM DeviceGroup
 *  Archive Log:    put all constants used in xml file to XMLConstants
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.api.management.devicegroups;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DGSelectAdapter extends XmlAdapter<String, DGSelect> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public DGSelect unmarshal(String v) throws Exception {
        for (DGSelect sel : DGSelect.values()) {
            if (sel.getName().equalsIgnoreCase(v)) {
                return sel;
            }
        }
        throw new IllegalArgumentException("Unknown DGSelect '" + v + "'");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public String marshal(DGSelect v) throws Exception {
        return v.getName();
    }

}