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
 *  File Name: SMFailoverException.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.1  2015/08/27 19:35:15  fernande
 *  Archive Log:    PR 128703 - Fail over doesn't work on A0 Fabric. Fixes for several issues found during testing
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2015/08/17 18:49:07  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - change backend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/03/24 18:40:47  robertja
 *  Archive Log:    Add exception for SM fail-over.
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: robertja
 *
 ******************************************************************************/

package com.intel.stl.fecdriver.dispatcher;

import com.intel.stl.api.FMRuntimeException;
import com.intel.stl.api.IMessage;

public class SMFailoverException extends FMRuntimeException {

    /**
     * Description: 
     *
     * @param message 
     */
    public SMFailoverException(IMessage message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * Description: 
     *
     * @param message
     * @param cause 
     */
    public SMFailoverException(IMessage message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * Description: 
     *
     * @param message
     * @param cause
     * @param arguments 
     */
    public SMFailoverException(IMessage message, Throwable cause,
            Object... arguments) {
        super(message, cause, arguments);
        // TODO Auto-generated constructor stub
    }

    /**
     * Description: 
     *
     * @param message
     * @param arguments 
     */
    public SMFailoverException(IMessage message, Object... arguments) {
        super(message, arguments);
        // TODO Auto-generated constructor stub
    }

}
