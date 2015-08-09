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
 *  File Name: ILoginAssistant.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.4  2015/04/29 22:02:09  jijunwan
 *  Archive Log:    changed DefaulLoginAssistant to be DOCUMENT_MODAL
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2015/04/28 21:55:01  jijunwan
 *  Archive Log:    improved LoginAssistant to support setting owner
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2015/03/10 22:41:41  jijunwan
 *  Archive Log:    improved to show progress while we log into a host
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/03/05 17:30:40  jijunwan
 *  Archive Log:    init version to support Application management
 *  Archive Log:    1) read/write opafm.xml from/to host with backup file support
 *  Archive Log:    2) Application parser
 *  Archive Log:    3) Add/remove and update Application
 *  Archive Log:    4) unique name, reference conflication check
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.api;


public interface ILoginAssistant extends IAssistant {
    void init(String hostname, String userName);

    int getPort();

    String getUserName();

    char[] getPassword();
}