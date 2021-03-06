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
 *  File Name: NoticeSaveTask.java
 *  
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.21  2015/09/26 06:17:56  jijunwan
 *  Archive Log:    130487 - FM GUI: Topology refresh required after enabling Fabric Simulator
 *  Archive Log:    - added more log info
 *  Archive Log:
 *  Archive Log:    Revision 1.20  2015/08/17 18:49:13  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - change backend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.19  2015/07/19 20:54:50  jijunwan
 *  Archive Log:    PR 126645 - Topology Page does not show correct data after port disable/enable event
 *  Archive Log:    - fixed sync issue on DB update and cached node distribution
 *  Archive Log:
 *  Archive Log:    Revision 1.18  2015/06/08 15:57:53  fernande
 *  Archive Log:    PR 128897 - STLAdapter worker thread is in a continuous loop, even when there are no requests to service. Stabilizing the new FEAdapter code. Fixing null condition where noticeWrappers are accessed by the NoticeManager and it's null
 *  Archive Log:
 *  Archive Log:    Revision 1.17  2015/05/19 19:07:17  jijunwan
 *  Archive Log:    PR 128797 - Notice update failed to update related notes
 *  Archive Log:    - created a new class NoticeWrapper to store information about related nodes, and then pass this infor to EventDescription that will allow UI to upate related nodes
 *  Archive Log:
 *  Archive Log:    Revision 1.16  2015/05/01 21:37:41  jijunwan
 *  Archive Log:    fixed typo found by FindBug
 *  Archive Log:
 *  Archive Log:    Revision 1.15  2015/04/06 21:14:13  fernande
 *  Archive Log:    Improving the handling of connection errors. When a connection error occurs, the request for PMConfig shouldn't wait but throw an exception, and this exception should not prevent the notices from being processed
 *  Archive Log:
 *  Archive Log:    Revision 1.14  2015/04/06 11:04:54  jypak
 *  Archive Log:    Klockwork: Back End Critical Without Unit Test. Open issues fixed.
 *  Archive Log:
 *  Archive Log:    Revision 1.13  2015/03/16 14:31:43  jijunwan
 *  Archive Log:    renamed DevieGroup to DefaultDeviceGroup because it's an enum of default DGs, plus we need to use DeviceGroup for the DG definition used in opafm.xml
 *  Archive Log:
 *  Archive Log:    Revision 1.12  2015/02/23 22:40:40  jijunwan
 *  Archive Log:    PR 126610 - Subnet summary under Home page does not show correct data after port disable/enable event
 *  Archive Log:     - changed to user trap data to identify source node
 *  Archive Log:     - improved to distinguish active and inactive nodes/links
 *  Archive Log:     - improved to include related nodes before and after a notice
 *  Archive Log:     - improved to wait PM until it has updated data after a notice
 *  Archive Log:     - other improvement that update cache properly
 *  Archive Log:
 *  Archive Log:    Revision 1.11  2015/02/06 20:44:02  jypak
 *  Archive Log:    Header comments fixed for archive log to be updated.
 *  Archive Log:
 *
 *  Overview: Process notices based on FM query. Update each caches and then
 *  update the notices in database according to the response for the process.
 *
 *  @author: jypak
 *
 ******************************************************************************/

package com.intel.stl.api.notice.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.notice.GenericNoticeAttrBean;
import com.intel.stl.api.notice.NoticeBean;
import com.intel.stl.api.notice.NoticeWrapper;
import com.intel.stl.api.notice.TrapType;
import com.intel.stl.api.performance.GroupInfoBean;
import com.intel.stl.api.performance.ImageIdBean;
import com.intel.stl.api.performance.PMConfigBean;
import com.intel.stl.api.performance.impl.PAHelper;
import com.intel.stl.api.subnet.DefaultDeviceGroup;
import com.intel.stl.api.subnet.GIDBean;
import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.impl.SAHelper;
import com.intel.stl.configuration.AsyncTask;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.datamanager.DatabaseManager;
import com.intel.stl.datamanager.NoticeStatus;
import com.intel.stl.fecdriver.messages.adapter.sa.trap.TrapDetail;

public class NoticeProcessTask extends AsyncTask<Future<Boolean>> {
    private static Logger log = LoggerFactory
            .getLogger(NoticeProcessTask.class);

    private final DatabaseManager dbMgr;

    private final String subnetName;

    private final CacheManager cacheMgr;

    private final SAHelper helper;

    private List<NoticeWrapper> noticeWrappers;

    public NoticeProcessTask(String subnetName, DatabaseManager dbMgr,
            CacheManager cacheMgr) {
        this.dbMgr = dbMgr;
        this.subnetName = subnetName;
        this.cacheMgr = cacheMgr;
        this.helper = cacheMgr.getSAHelper();
        this.noticeWrappers = new ArrayList<NoticeWrapper>();
    }

    /**
     * @return the noticePrcs
     */
    public List<NoticeWrapper> getNoticeWrappers() {
        return noticeWrappers;
    }

    /**
     * process the list of notices. The thread pool size is 2, so, two
     * NoticeProcessingService can be processing at same time, so make this a
     * synchronized method.
     * 
     */
    @Override
    public Future<Boolean> process() throws Exception {
        // This is done to make sure that the subnet is defined in the database
        // and that a topology has been saved for it.
        dbMgr.getTopologyId(subnetName);

        // This atomically gets notices in RECEIVED status and change their
        // status to INFLIGHT
        List<NoticeBean> notices =
                dbMgr.getNotices(subnetName, NoticeStatus.RECEIVED,
                        NoticeStatus.INFLIGHT);
        log.info("Retrieving " + notices.size()
                + " notices in the background for subnet: " + subnetName);

        if (notices.size() == 0) {
            log.info("No notices to process for subnet: " + subnetName);
            return null;
        }

        // PM doesn't update itself immediately after notices. It update in the
        // next sweep. To ensure we get correct data, we need wait here until
        // we are sure PM is updated, i.e. latest image number changed at least
        // once
        long t = System.currentTimeMillis();
        boolean success;
        try {
            // When there is a connection error, this will fail, causing the
            // notice not to be processed
            success = waitPM();
        } catch (Exception e) {
            success = false;
        }
        log.info("waited " + (System.currentTimeMillis() - t)
                + " ms for PM. State: success=" + success);

        // prepare notices
        noticeWrappers = new ArrayList<NoticeWrapper>(notices.size());
        for (NoticeBean notice : notices) {
            NoticeWrapper nw = prepareNotice(notice);
            noticeWrappers.add(nw);
        }

        // We pack as much work as possible using multiple threads; but first we
        // need to get information from the FM
        List<NoticeProcess> noticePrcs = createNoticeProcesses(noticeWrappers);

        // Start the thread to update the database; no wait, just keep a hold of
        // the Future.
        Future<Boolean> result = dbMgr.processNotices(subnetName, noticePrcs);

        // And now let the CacheManager update its caches in parallel. Keep
        // in mind that nodes, links and ports are seen by two threads
        // concurrently, so in the database be careful to update only persisted
        // status fields (in TopologyNodeRecord and in TopologyLinkRecord),
        // never the objects themselves, which can potentially affect the caches
        // themselves.
        // Please note that some caches, such as DBNodeCache, rely on DB, i.e.
        // topology update. This will cause synchronization issues!! It's
        // important to update these caches again after DB update is done. See
        // SubnetContextImpl#processNotices for the code.
        for (NoticeProcess noticeProcess : noticePrcs) {
            try {
                cacheMgr.updateCaches(noticeProcess);
                // Update the complete flag for each notice.
                if (noticeProcess.getNotice() != null) {
                    dbMgr.updateNotice(subnetName, noticeProcess.getNotice()
                            .getId(), NoticeStatus.PROCESSED);
                }
            } catch (Exception e) {
                log.error("Error while updating caches for notice "
                        + noticeProcess.getNotice().getId() + ": "
                        + noticeProcess.getNotice(), e);
                if (noticeProcess.getNotice() != null) {
                    dbMgr.updateNotice(subnetName, noticeProcess.getNotice()
                            .getId(), NoticeStatus.FEERROR);
                }
            }
        }
        log.info("Notices have been processed");
        return result;
    }

    private NoticeWrapper prepareNotice(NoticeBean notice) {
        TrapType trapType = getTrapType(notice);
        NoticeWrapper nw = new NoticeWrapper(notice, trapType);
        long guid = -1;
        int lid = -1;
        NodeRecordBean node = null;
        try {
            switch (trapType) {
                case GID_NOW_IN_SERVICE: {
                    GIDBean gid = TrapDetail.getGID(notice.getData());
                    guid = gid.getInterfaceID();
                    node = helper.getNode(guid);
                    if (node != null) {
                        lid = node.getLid();
                        List<LinkRecordBean> links = helper.getLinks(lid);
                        nw.addRelatedNodes(getRelatedNodes(links));
                    }
                    break;
                }
                case GID_OUT_OF_SERVICE: {
                    GIDBean gid = TrapDetail.getGID(notice.getData());
                    guid = gid.getInterfaceID();
                    node = dbMgr.getNode(subnetName, guid);
                    if (node != null) {
                        lid = node.getLid();
                        LinkRecordBean link =
                                dbMgr.getLinkBySource(subnetName, lid,
                                        (short) 1);
                        nw.addRelatedNode(link.getToLID());
                    }
                    break;
                }
                case LINK_PORT_CHANGE_STATE:
                    lid = TrapDetail.getLid(notice.getData());
                    node = helper.getNode(lid);
                    List<LinkRecordBean> links =
                            dbMgr.getLinks(subnetName, lid);
                    nw.addRelatedNodes(getRelatedNodes(links));
                    links = helper.getLinks(lid);
                    nw.addRelatedNodes(getRelatedNodes(links));
                    break;
                default:
                    log.warn("Unsupported notice " + notice);
            }
            if (node == null) {
                log.error("Node information not found in FM or DB for GUID="
                        + StringUtils.longHexString(guid) + " or LID=" + lid
                        + " mentioned in notice: " + notice);
                dbMgr.updateNotice(subnetName, notice.getId(),
                        NoticeStatus.FEERROR);
            }
        } catch (Exception e) {
            log.error("Error while preparing notice " + notice.getId() + ": "
                    + notice, e);
            dbMgr.updateNotice(subnetName, notice.getId(), NoticeStatus.FEERROR);
        }
        nw.setNode(node);
        return nw;
    }

    private List<NoticeProcess> createNoticeProcesses(
            List<NoticeWrapper> noticeWrappers) {
        List<NoticeProcess> noticePrcs = new ArrayList<NoticeProcess>();
        for (NoticeWrapper nw : noticeWrappers) {
            try {
                NodeRecordBean node = nw.getNode();
                if (node != null) {
                    NoticeProcess np =
                            createNoticeProcess(nw.getNotice(),
                                    nw.getTrapType(), node);
                    if (np != null) {
                        noticePrcs.add(np);
                    }

                    for (int relatedLid : nw.getRelatedNodes()) {
                        node = helper.getNode(relatedLid);
                        np =
                                createNoticeProcess(null,
                                        TrapType.LINK_PORT_CHANGE_STATE, node);
                        if (np != null) {
                            noticePrcs.add(np);
                        }
                    }
                }
            } catch (Exception e) {
                NoticeBean notice = nw.getNotice();
                log.error("Error while processing notice " + notice.getId()
                        + ": " + notice, e);
                dbMgr.updateNotice(subnetName, notice.getId(),
                        NoticeStatus.FEERROR);
                continue;
            }
        }

        return noticePrcs;
    }

    protected Set<Integer> getRelatedNodes(List<LinkRecordBean> links) {
        Set<Integer> res = new HashSet<Integer>();
        for (LinkRecordBean link : links) {
            res.add(link.getToLID());
        }
        return res;
    }

    protected NoticeProcess createNoticeProcess(NoticeBean notice,
            TrapType trapType, NodeRecordBean node) throws Exception {
        NoticeProcess np = new NoticeProcess(notice);
        np.setTrapType(trapType);
        int lid = 0;
        if (node != null) {
            lid = node.getLid();
        } else {
            return null;
        }

        np.setLid(lid);
        np.setNode(node);
        // For the current three trap types we support we would need to
        // refresh port information; this might need to be fine tuned when other
        // traps are added
        np.setPorts(helper.getPorts(lid));
        np.setLinks(helper.getLinks(lid));
        return np;
    }

    private TrapType getTrapType(NoticeBean notice) {
        // We only get Generic now.
        // Retrieve Node LID based on the notice
        GenericNoticeAttrBean attr =
                (GenericNoticeAttrBean) notice.getAttributes();
        return TrapType.getTrapType(attr.getTrapNumber());
    }

    /**
     * 
     * <i>Description:</i> wait until PM get updated for the notices. In the
     * worst case we will need to wait for sweep time. Whether PM is updated is
     * judged by the change of image number
     * 
     */
    protected boolean waitPM() throws Exception {
        PAHelper helper = cacheMgr.getPAHelper();
        PMConfigBean conf = helper.getPMConfig();
        int sweep = 0;
        if (conf != null) {
            sweep = conf.getSweepInterval(); // in seconds
        }
        GroupInfoBean gi =
                helper.getGroupInfo(DefaultDeviceGroup.ALL.getName());
        ImageIdBean image = null;
        if (gi != null) {
            image = gi.getImageId();
        }
        long id = 0L;
        if (image != null) {
            id = image.getImageNumber();
        }
        int count = (int) (sweep * 1.1 / 0.2);
        long imageNumber = id;
        while (id == imageNumber && count > 0) {
            Thread.sleep(200);
            gi = helper.getGroupInfo(DefaultDeviceGroup.ALL.getName());
            if (gi != null) {
                image = gi.getImageId();
                if (image != null) {
                    imageNumber = image.getImageNumber();
                }
            }
            count -= 1;
        }
        return count > 0;
    }

}
