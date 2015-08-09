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
 *  File Name: CancellableCallback.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.1  2014/08/05 13:42:43  jijunwan
 *  Archive Log:    added special caller and callback to support cancellation
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.ui.publisher;

import com.intel.stl.ui.common.ICancelIndicator;

public abstract class CancellableCallback<E> implements ICallback<E> {
    private ICancelIndicator cancelIndicator;

    /**
     * @param indicator the indicator to set
     */
    public void setCancelIndicator(ICancelIndicator indicator) {
        this.cancelIndicator = indicator;
    }

    /* (non-Javadoc)
     * @see com.intel.stl.ui.publisher.ICallback#onDone(java.lang.Object)
     */
    @Override
    public void onDone(E result) {
        onDone(result, cancelIndicator);
    }

    /* (non-Javadoc)
     * @see com.intel.stl.ui.publisher.ICallback#onError(java.lang.Throwable[])
     */
    @Override
    public void onError(Throwable... errors) {
    }

    /* (non-Javadoc)
     * @see com.intel.stl.ui.publisher.ICallback#onProgress(double)
     */
    @Override
    public void onProgress(double progress) {
    }

    /* (non-Javadoc)
     * @see com.intel.stl.ui.publisher.ICallback#onFinally()
     */
    @Override
    public void onFinally() {
    }
    

    protected abstract void onDone(E result, ICancelIndicator indicator); 
}