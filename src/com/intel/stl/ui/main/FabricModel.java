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
 *  File Name: FabricModel.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.3  2015/08/17 18:53:38  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - changed frontend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/10/22 01:16:19  jijunwan
 *  Archive Log:    some simplifications on MVC framework
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/10/09 12:59:15  fernande
 *  Archive Log:    Changed the FabricController to use the UI framework and converted Swing workers into AbstractTasks to optimize the switching of contexts and the refreshing of pages. These processes still run under Swing workers, but now each setContext is run on its own Swing worker to improve performance. Also, changed the ProgressObserver mechanism to provide a more accurate progress.
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: fernande
 *
 ******************************************************************************/

package com.intel.stl.ui.main;

import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.ui.framework.AbstractModel;

public class FabricModel extends AbstractModel {

    private SubnetDescription currentSubnet;

    private SubnetDescription previousSubnet;

    private boolean addRandomValues;

    private String errorMessage;

    public SubnetDescription getCurrentSubnet() {
        return currentSubnet;
    }

    public void setCurrentSubnet(SubnetDescription currentSubnet) {
        this.currentSubnet = currentSubnet;
    }

    public SubnetDescription getPreviousSubnet() {
        return previousSubnet;
    }

    public void setPreviousSubnet(SubnetDescription previousSubnet) {
        this.previousSubnet = previousSubnet;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isAddRandomValues() {
        return addRandomValues;
    }

    public void setAddRandomValues(boolean addRandomValues) {
        this.addRandomValues = addRandomValues;
    }

}
