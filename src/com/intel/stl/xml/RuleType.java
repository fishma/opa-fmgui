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
 *  File Name: RuleType.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.1  2014/11/26 18:56:39  fernande
 *  Archive Log:    Adding support to save EventRules in the UserOptions XML
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: fernande
 *
 ******************************************************************************/

package com.intel.stl.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for RuleType.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name="RuleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SM_TOPO_CHANGE"/>
 *     &lt;enumeration value="PORT_ACTIVE"/>
 *     &lt;enumeration value="PORT_INACTIVE"/>
 *     &lt;enumeration value="FE_CONNECTION_LOST"/>
 *     &lt;enumeration value="FE_CONNECTION_ESTABLISH"/>
 *     &lt;enumeration value="SM_CONNECTION_LOST"/>
 *     &lt;enumeration value="SM_CONNECTION_ESTABLISH"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RuleType")
@XmlEnum
public enum RuleType {

    SM_TOPO_CHANGE,
    PORT_ACTIVE,
    PORT_INACTIVE,
    FE_CONNECTION_LOST,
    FE_CONNECTION_ESTABLISH,
    SM_CONNECTION_LOST,
    SM_CONNECTION_ESTABLISH;

    public String value() {
        return name();
    }

    public static RuleType fromValue(String v) {
        return valueOf(v);
    }

}