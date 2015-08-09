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
 *  File Name: CacheManager.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.15  2015/03/27 20:42:26  fernande
 *  Archive Log:    Changed to use the new SerialProcessingService interface
 *  Archive Log:
 *  Archive Log:    Revision 1.14  2015/02/23 22:24:35  jijunwan
 *  Archive Log:    added GroupConfCache
 *  Archive Log:
 *  Archive Log:    Revision 1.13  2014/12/31 17:41:16  jypak
 *  Archive Log:    1. CableInfo updates (Moved the QSFP interpretation logic to backend etc.)
 *  Archive Log:    2. SC2SL updates.
 *  Archive Log:    3. SC2VLt updates.
 *  Archive Log:    4. SC3VLnt updates.
 *  Archive Log:    Some of the SubnetApi, CachedSubnetApi updates should be undone when the FE supports cable info, SC2SL, SC2VLt, SC2VLnt.
 *  Archive Log:
 *  Archive Log:    Revision 1.12  2014/12/18 16:25:40  jypak
 *  Archive Log:    Cable Info updates.
 *  Archive Log:
 *  Archive Log:    Revision 1.11  2014/11/05 22:42:04  fernande
 *  Archive Log:    Adding integration testing for the NoticeManager connecting to a simulated fabric. The test is not complete though, we need to connect thru SSH to the fabric simulator and play with the fabric.
 *  Archive Log:
 *  Archive Log:    Revision 1.10  2014/10/24 18:49:56  fernande
 *  Archive Log:    Initial batch of fixes for notice processing. Notice updates not working yet but need a stable environment for Bob.
 *  Archive Log:
 *  Archive Log:    Revision 1.9  2014/10/14 20:44:46  jijunwan
 *  Archive Log:    1) improved to set SubnetContext invalid when we have network connection issues
 *  Archive Log:    2) improved to recreate SubnetContext when we query for it and the current one is invalid. We also clean up (include shutdown) the old context before we replace it with a new one
 *  Archive Log:
 *  Archive Log:    Revision 1.8  2014/09/22 11:35:55  jypak
 *  Archive Log:    Added a new CopyTopologyTask and related updates for the subnet DAO and the cache manager.
 *  Archive Log:
 *  Archive Log:    Revision 1.7  2014/09/18 20:48:50  fernande
 *  Archive Log:    Enabling GroupInfo saving after fixing issues in the application
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2014/09/17 13:17:35  jypak
 *  Archive Log:    Return boolean for each cache process notice operation to let CacheManager know whether it need to start the CopyTopologyTask.
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2014/09/15 15:15:40  jypak
 *  Archive Log:    Notice Manager JUnit tests and relevant fixes.
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2014/09/12 19:58:06  fernande
 *  Archive Log:    We now save ImageInfo and GroupInfo to the database. As they are retrieved by the UI, they are buffered and then saved at certain thresholds.
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/08/28 14:56:57  jypak
 *  Archive Log:    Notice Manager updates.
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/07/07 21:19:48  fernande
 *  Archive Log:    Adding method to trigger a topology update from a managed cache
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/07/03 21:34:19  fernande
 *  Archive Log:    Adding the CacheManager in support of APIs
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: fernande
 *
 ******************************************************************************/

package com.intel.stl.configuration;

import com.intel.stl.api.notice.impl.NoticeProcess;
import com.intel.stl.api.performance.impl.GroupCache;
import com.intel.stl.api.performance.impl.GroupConfCache;
import com.intel.stl.api.performance.impl.PAHelper;
import com.intel.stl.api.subnet.impl.CableCache;
import com.intel.stl.api.subnet.impl.LFTCache;
import com.intel.stl.api.subnet.impl.LinkCache;
import com.intel.stl.api.subnet.impl.MFTCache;
import com.intel.stl.api.subnet.impl.NodeCache;
import com.intel.stl.api.subnet.impl.PKeyTableCache;
import com.intel.stl.api.subnet.impl.PortCache;
import com.intel.stl.api.subnet.impl.SAHelper;
import com.intel.stl.api.subnet.impl.SC2SLMTCache;
import com.intel.stl.api.subnet.impl.SC2VLNTMTCache;
import com.intel.stl.api.subnet.impl.SC2VLTMTCache;
import com.intel.stl.api.subnet.impl.SMCache;
import com.intel.stl.api.subnet.impl.SwitchCache;
import com.intel.stl.api.subnet.impl.VLArbTableCache;
import com.intel.stl.datamanager.DatabaseManager;
import com.intel.stl.fecdriver.impl.FEHelper;

public interface CacheManager {

    public static final String SA_HELPER = "SAHelper";

    public static final String PA_HELPER = "PAHelper";

    public static final String TOPOLOGY_UPDATE_TASK = "TopologyUpdate";

    FEHelper getHelper(String helperName);

    SAHelper getSAHelper();

    PAHelper getPAHelper();

    SerialProcessingService getProcessingService();

    void startTopologyUpdateTask();

    DatabaseManager getDatabaseManager();

    NodeCache acquireNodeCache();

    LinkCache acquireLinkCache();

    PortCache acquirePortCache();

    SwitchCache acquireSwitchCache();

    LFTCache acquireLFTCache();

    MFTCache acquireMFTCache();

    PKeyTableCache acquirePKeyTableCache();

    VLArbTableCache acquireVLArbTableCache();

    SMCache acquireSMCache();

    CableCache acquireCableCache();

    GroupCache acquireGroupCache();

    GroupConfCache acquireGroupConfCache();

    SC2SLMTCache acquireSC2SLMTCache();

    SC2VLTMTCache acquireSC2VLTMTCache();

    SC2VLNTMTCache acquireSC2VLNTMTCache();

    void updateCaches(NoticeProcess notice) throws Exception;

    void cleanup();
}