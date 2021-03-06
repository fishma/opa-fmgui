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
 *  File Name: ICancellableCall.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.4  2015/08/17 18:54:09  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - changed frontend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2015/02/02 14:20:23  jijunwan
 *  Archive Log:    improvement to CancellableCall to handle null cancel indicator and indicator is set after the caller already ran
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/10/09 21:30:48  jijunwan
 *  Archive Log:    promote cancelIndicator to protected
 *  Archive Log:
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

import java.util.concurrent.Callable;

import com.intel.stl.ui.common.ICancelIndicator;

public abstract class CancellableCall<V> implements Callable<V> {
    protected ICancelIndicator cancelIndicator;

    /**
     * @param cancelIndicator
     *            the cancelIndicator to set
     */
    public void setCancelIndicator(ICancelIndicator cancelIndicator) {
        this.cancelIndicator = cancelIndicator;
    }

    /**
     * @return the cancelIndicator
     */
    public ICancelIndicator getCancelIndicator() {
        return cancelIndicator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public V call() throws Exception {
        // a wrapper of cancelIndicator to handle the following case:
        // 1) cancelIndicator is null
        // 2) cancelIndicator is set after the call already ran
        return call(new ICancelIndicator() {

            @Override
            public boolean isCancelled() {
                return cancelIndicator != null && cancelIndicator.isCancelled();
            }

        });
    }

    public abstract V call(ICancelIndicator cancelIndicator) throws Exception;
}
