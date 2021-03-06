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
 *  File Name: HistoryQueryTask.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.4  2015/08/17 18:54:08  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - changed frontend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2015/03/02 15:28:07  jypak
 *  Archive Log:    History query has been done with current live image ID '0' which isn't correct. Updates here are:
 *  Archive Log:    1. Get the image ID from current image.
 *  Archive Log:    2. History queries are done with this image ID.
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2015/02/12 19:40:04  jijunwan
 *  Archive Log:    short term PA support
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/02/10 21:25:59  jypak
 *  Archive Log:    1. Introduced SwingWorker for history query initialization for progress status updates.
 *  Archive Log:    2. Fixed the list of future for history query in TaskScheduler. Now it can have all the Future entries created.
 *  Archive Log:    3. When selecting history type, just cancel the history query not sheduled query.
 *  Archive Log:    4. The refresh rate is now from user settings not from the config api.
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jypak
 *
 ******************************************************************************/

package com.intel.stl.ui.publisher;

import java.util.concurrent.Future;

import com.intel.stl.api.ITimestamped;
import com.intel.stl.api.performance.ImageIdBean;
import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.model.HistoryType;

public abstract class HistoryQueryTask<E extends ITimestamped> extends
        CancellableCall<Void> {
    private final double step;

    private final int maxDataPoints;

    private final int lengthInSeconds;

    private final ICallback<E[]> callback;

    /**
     * Description:
     * 
     * @param step
     */
    public HistoryQueryTask(double step, int refreshRate, HistoryType type,
            ICallback<E[]> callback) {
        super();
        this.step = step;
        this.maxDataPoints = type.getMaxDataPoints(refreshRate);
        this.lengthInSeconds = type.getLengthInSeconds();
        this.callback = callback;
    }

    public void setFuture(final Future<Void> future) {
        setCancelIndicator(new ICancelIndicator() {
            @Override
            public boolean isCancelled() {
                return future.isCancelled();
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.publisher.CancellableCall#call(com.intel.stl.ui.common
     * .ICancelIndicator)
     */
    @Override
    public Void call(ICancelIndicator cancelIndicator) throws Exception {
        long firstTime = -1;

        ImageIdBean[] imageIdBeans = queryImageId();
        int imageIdBeanLength = imageIdBeans.length;
        long[] imageIds = new long[imageIdBeanLength];
        for (int j = 0; j < imageIdBeanLength; j++) {
            imageIds[j] = imageIdBeans[j].getImageNumber();
        }

        for (int i = 0; i < maxDataPoints && !cancelIndicator.isCancelled(); i++) {
            int offset = (int) ((-i - 1) * step);
            E[] datapoints = queryHistory(imageIds, offset);
            if (datapoints != null && datapoints.length > 0) {
                E datapoint = datapoints[0];
                long time = datapoint.getTimestamp();
                if (firstTime == -1) {
                    firstTime = time;
                } else if (firstTime - time > lengthInSeconds) {
                    break;
                }
                callback.onDone(datapoints);
            } else {
                break;
            }
        }
        return null;
    }

    protected abstract ImageIdBean[] queryImageId();

    protected abstract E[] queryHistory(long[] imageIDs, int offset);
}
