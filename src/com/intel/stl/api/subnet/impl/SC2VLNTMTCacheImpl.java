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
 *  File Name: SC2VLNTMTCacheImpl.java
 *
 *  Overview: 
 *
 *  @author: jypak
 *
 ******************************************************************************/

package com.intel.stl.api.subnet.impl;

import java.util.ArrayList;
import java.util.List;

import com.intel.stl.api.notice.impl.NoticeProcess;
import com.intel.stl.api.subnet.SC2VLMTRecordBean;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.configuration.MemoryCache;

public class SC2VLNTMTCacheImpl extends MemoryCache<List<SC2VLMTRecordBean>>
        implements SC2VLNTMTCache {

    private final SAHelper helper;

    public SC2VLNTMTCacheImpl(CacheManager cacheMgr) {
        super(cacheMgr);
        this.helper = cacheMgr.getSAHelper();
    }

    @Override
    public List<SC2VLMTRecordBean> getSC2VLNTMTs() {
        List<SC2VLMTRecordBean> res = getCachedObject();
        return res;
    }

    @Override
    public List<SC2VLMTRecordBean> getSC2VLNTMT(int lid) {
        List<SC2VLMTRecordBean> sc2vlntmts = getSC2VLNTMTs();
        List<SC2VLMTRecordBean> res = new ArrayList<SC2VLMTRecordBean>();
        if (sc2vlntmts != null) {
            for (SC2VLMTRecordBean sc2vlnt : sc2vlntmts) {
                if (sc2vlnt.getLid() == lid) {
                    res.add(sc2vlnt);
                }
            }
        }
        if (!res.isEmpty()) {
            return res;
        }
        // might be a new node
        try {
            res = helper.getSC2VLNTMT(lid);
            if (res != null && !res.isEmpty()) {
                setCacheReady(false); // Force a refresh on next call;
            }
        } catch (Exception e) {
            log.error("Error getting Cable Infos by lid " + lid, e);
            e.printStackTrace();
            throw SubnetApi.getSubnetException(e);
        }
        return res;
    }

    @Override
    public SC2VLMTRecordBean getSC2VLNTMT(int lid, short portNum) {
        List<SC2VLMTRecordBean> sc2vlntmts = getSC2VLNTMTs();
        if (sc2vlntmts != null) {
            for (SC2VLMTRecordBean sc2vlnt : sc2vlntmts) {
                if (sc2vlnt.getLid() == lid && sc2vlnt.getPort() == portNum) {
                    return sc2vlnt;
                }
            }
        }

        // might be a new node
        try {
            List<SC2VLMTRecordBean> sc2vlntFromFE = helper.getSC2VLNTMT(lid);
            if (sc2vlntFromFE != null && !sc2vlntFromFE.isEmpty()) {
                setCacheReady(false); // Force a refresh on next call;
                for (SC2VLMTRecordBean sc2vlnt : sc2vlntFromFE) {
                    if (sc2vlnt.getPort() == portNum) {
                        return sc2vlnt;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting SC2VLnt Infos by lid " + lid
                    + ", portNum=" + portNum, e);
            e.printStackTrace();
            throw SubnetApi.getSubnetException(e);
        }

        // TODO: Should throw an exception?
        return null;
    }

    @Override
    protected List<SC2VLMTRecordBean> retrieveObjectForCache() throws Exception {
        List<SC2VLMTRecordBean> res = helper.getSC2VLNTMTs();
        log.info("Retrieve " + (res == null ? 0 : res.size())
                + " SC2VLNTMT Infos from FE");
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.configuration.BaseCache#refreshCache(com.intel.stl.api.
     * notice.impl.NoticeProcess)
     */
    @Override
    public boolean refreshCache(NoticeProcess notice) throws Exception {
        // No notice applies to this cache
        return true;
    }

    @Override
    protected RuntimeException processRefreshCacheException(Exception e) {
        return SubnetApi.getSubnetException(e);
    }
}