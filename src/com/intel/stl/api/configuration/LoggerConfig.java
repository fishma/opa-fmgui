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
 *  File Name: LoggerConfig.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.3  2015/08/17 18:48:36  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - change backend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2015/06/26 13:36:56  rjtierne
 *  Archive Log:    PR 128975 - Can not setup application log
 *  Archive Log:    Override toString() to provide a better description of class attributes during debug.
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/06/10 19:13:59  rjtierne
 *  Archive Log:    PR 128975 - Can not setup application log
 *  Archive Log:    Initial Version
 *  Archive Log:
 *
 *  Overview: Bean to hold a logger name and its level
 *
 *  @author: rjtierne
 *
 ******************************************************************************/

package com.intel.stl.api.configuration;

import java.io.Serializable;

public class LoggerConfig implements Serializable {

    private static final long serialVersionUID = -6482356522346140482L;

    private String name;

    private LoggingThreshold level;

    public LoggerConfig(String name, LoggingThreshold level) {

        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LoggingThreshold getLevel() {
        return level;
    }

    public void setLevel(LoggingThreshold level) {
        this.level = level;
    }

    @Override
    public String toString() {

        return "LoggerConfig [name=" + getName() + ", level=" + getLevel()
                + "]";
    }
}
