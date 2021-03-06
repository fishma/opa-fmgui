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
 *  File Name: IConsoleLogin.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.6  2015/08/17 18:54:27  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - changed frontend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2015/05/27 14:34:51  rjtierne
 *  Archive Log:    128874 - Eliminate login dialog from admin console and integrate into panel
 *  Archive Log:    - Renamed showDialog() and hideDialog() to showLogin() and hideLogin()
 *  Archive Log:    - Added field setters/getters to the interface
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2014/10/24 14:36:48  rjtierne
 *  Archive Log:    Added consoleId parameter to showDialog() prototype
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/09/23 19:47:00  rjtierne
 *  Archive Log:    Integration of Gritty for Java Console
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/09/09 20:03:28  rjtierne
 *  Archive Log:    Added default login bean to console dialog to reduce typing
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/09/09 14:17:53  rjtierne
 *  Archive Log:    Initial Version
 *  Archive Log:
 *
 *  Overview: Interface for the consoleLogin
 *
 *  @author: rjtierne
 *
 ******************************************************************************/
package com.intel.stl.ui.console;

public interface IConsoleLogin {

    public void hideLogin();

    public void showLogin(final LoginBean loginBean);

    public void showLogin(LoginBean loginBean, boolean newConsole, int consoleId);

    public void showMessage(String message);

    public void startProgress();

    public void killProgress();

    public String getUserName();

    public char[] getPassword();

    public String getHostName();

    public String getPortNum();

    public void setConsoleEventListener(
            IConsoleEventListener consoleEventListener);
}
