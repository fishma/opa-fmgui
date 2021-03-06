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
 *  File Name: MemoryCache.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.4  2015/09/26 06:17:08  jijunwan
 *  Archive Log:    130487 - FM GUI: Topology refresh required after enabling Fabric Simulator
 *  Archive Log:    - added reset to clear all caches and update DB topology
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2015/08/17 18:48:40  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - change backend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/11/04 14:14:41  fernande
 *  Archive Log:    NoticeManager performance improvements. Notices are now processed in batches and database update is done in parallel with cache updates. Changes to the management of caches; if a cache is not ready, no updates for a notice are carried out.
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/09/17 16:44:03  fernande
 *  Archive Log:    Refactored CacheManager to load caches according to what's defined in enums MemCacheType and DBCacheType, to make it more dynamic and more extensible. Changed code so that Exception caught during refreshCache is converted into a RuntimeException and it's rethrown every time getCachedObject is invoked. This to reflect that the caller has not much to do with the original error.
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2014/08/28 14:56:50  jypak
 *  Archive Log:    Notice Manager updates.
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2014/08/12 20:27:41  jijunwan
 *  Archive Log:    1) changed specific xxxxNotFoundExceptions to SubnetDataNotFoundException or PerformanceDataNotFoundException
 *  Archive Log:    2) added throws SubnetException to ISubnetApi
 *  Archive Log:    3) added throws PerformanceException to IPerformanceApi
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2014/08/12 13:10:23  fernande
 *  Archive Log:    Adding support for Notice processing
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/07/09 12:47:12  fernande
 *  Archive Log:    Saving the exception, if any, at the time the cache was refreshed
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/07/07 16:50:39  fernande
 *  Archive Log:    Changing WeakReferences to SoftReference and List to Map for NodeCache and PortCache
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/07/03 21:35:54  fernande
 *  Archive Log:    Adding the CacheManager in support of APIs
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: fernande
 *
 ******************************************************************************/

package com.intel.stl.configuration;

import static com.intel.stl.common.STLMessages.STL60006_EXCEPTION_REFRESHING_CACHE;

import java.lang.ref.SoftReference;

public abstract class MemoryCache<H> extends BaseCache {
    public static final long DEFAULT_TICK_RESOLUTION = 9000; // 1 sec

    private SoftReference<H> cachedObjectReference;

    // Whatever the Exception received during refreshCache, it should be
    // converted into a RuntimeException by the cache, to reflect the fact that
    // the caller of the cache's interface cannot do much about it. Override
    // processRefreshCacheException() to create your own RuntimeException
    private RuntimeException refreshException;

    private long tickResolution = DEFAULT_TICK_RESOLUTION;

    public MemoryCache(CacheManager cacheMgr) {
        super(cacheMgr);
        this.cachedObjectReference = new SoftReference<H>(null);
    }

    /**
     * @return the tickResolution
     */
    public long getTickResolution() {
        return tickResolution;
    }

    /**
     * @param tickResolution
     *            the tickResolution to set
     */
    public void setTickResolution(long tickResolution) {
        this.tickResolution = tickResolution;
    }

    /**
     * 
     * <i>Description:</i> returns the cached object. The only time the cached
     * object should be null is when there is an exception while refreshing the
     * cachedObject; retrieveObjectForCache() should return an empty object if
     * not data is available. What this empty object represents for the cache is
     * implementation-dependent.
     * 
     * @return the cached object
     */
    protected H getCachedObject() {
        if (refreshException != null) {
            throw refreshException;
        }
        H res = cachedObjectReference.get();
        if (res == null) {
            updateCache();
            res = cachedObjectReference.get();
        }
        return res;
    }

    protected void setCachedObject(H cachedObject) {
        cachedObjectReference = new SoftReference<H>(cachedObject);
    }

    protected RuntimeException getRefreshException() {
        return refreshException;
    }

    @Override
    public boolean isCacheReady() {
        if (cachedObjectReference.get() == null) {
            cacheReady.set(false);
        }
        return cacheReady.get();
    }

    /**
     * cache should be consistent with data retrieved from FE. If error on FE
     * side leads null value or exceptions, cache should reflect this so our UI
     * will not mislead our user. If we do not update cache to reflect errors on
     * FM side, a user may think the fabric is still working fine and then make
     * a wrong decision. A special case we may argue is that when one device
     * changed from active to inactive, this will cause null or empty value from
     * FE without exception. Ideally this case should be handled through notice.
     * If not, we should set cache to null or empty to ensure consistency. So a
     * caller will be able to figure out something happened. Otherwise, there is
     * no way to know the change.
     */
    @Override
    public boolean refreshCache() {
        refreshException = null;
        try {
            H cachedObject = retrieveObjectForCache();
            setCachedObject(cachedObject);
            // reset refreshException if everything is fine
            refreshException = null;
            return true;
        } catch (Exception e) {
            setCachedObject(null);
            refreshException = processRefreshCacheException(e);
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.configuration.ManagedCache#reset()
     */
    @Override
    public void reset() {
        setCachedObject(null);
    }

    protected abstract H retrieveObjectForCache() throws Exception;

    protected RuntimeException processRefreshCacheException(Exception e) {
        String errorMsg =
                STL60006_EXCEPTION_REFRESHING_CACHE.getDescription(this
                        .getClass().getSimpleName(), e.getMessage());
        log.error(errorMsg, e);
        return new RuntimeException(errorMsg, e);
    }

}
