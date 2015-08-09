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
 *  File Name: FinishObserver.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.4  2014/10/09 12:51:45  fernande
 *  Archive Log:    Defines a page weight that can be assigned to a controller implementing the IContextAware interface. The main issue is that ProgressObservers used to use 100 as the base to calculate the progress of a context switch or a refresh; as this amount was divided up by sub ProgressObservers, the amounts to each observer would be rounded and precision would get lost, resulting in a non-accurate progress bar. The current implementation uses a MainframeController-defined property which is passed from the observers and subobservers to the controller with the exact amount being reported and a more exact progress value can be calculated from the total weight involved.
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/08/26 14:05:12  jijunwan
 *  Archive Log:    added sub-observers generation
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/08/05 13:36:44  jijunwan
 *  Archive Log:    fixed typo isCanceled->isCanelled, added cancel interface
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/06/27 22:22:21  jijunwan
 *  Archive Log:    added running indicator to Performance Subpages
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.ui.common;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class FinishObserver implements IProgressObserver {

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IProgressObserver#setProgress(double)
     */
    @Override
    public void publishProgress(double percentage) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IProgressObserver#setNote(java.lang.String)
     */
    @Override
    public void publishNote(String note) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IProgressObserver#isCanceled()
     */
    @Override
    public boolean isCancelled() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IProgressObserver#createSubObservers(int)
     */
    @Override
    public IProgressObserver[] createSubObservers(final int size) {
        IProgressObserver[] res = new FinishObserver[size];
        final IProgressObserver parent = this;
        final AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < size; i++) {
            res[i] = new FinishObserver() {
                @Override
                public void onFinish() {
                    if (count.incrementAndGet() == size) {
                        parent.onFinish();
                    }
                }
            };
        }
        return res;
    }

}