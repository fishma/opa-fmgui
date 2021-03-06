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
 *  File Name: FabricView.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.5  2015/08/17 18:54:02  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - changed frontend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2015/06/25 20:24:57  jijunwan
 *  Archive Log:    Bug 126755 - Pin Board functionality is not working in FV
 *  Archive Log:    - applied pin framework on fabric viewer and simple 'static' cards
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2015/03/10 18:43:11  jypak
 *  Archive Log:    JavaHelp System introduced to enable online help.
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/10/22 01:16:57  jijunwan
 *  Archive Log:    some simplifications on MVC framework
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/10/09 13:00:25  fernande
 *  Archive Log:    Changed the FabricController to use the UI framework and converted Swing workers into AbstractTasks to optimize the switching of contexts and the refreshing of pages. These processes still run under Swing workers, but now each setContext is run on its own Swing worker to improve performance. Also, changed the ProgressObserver mechanism to provide a more accurate progress.
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: fernande
 *
 ******************************************************************************/

package com.intel.stl.ui.main.view;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.intel.stl.ui.framework.AbstractView;
import com.intel.stl.ui.main.FabricController;
import com.intel.stl.ui.main.FabricModel;

/**
 * This component is just a passthrough between FabricController and FVMainFrame
 * for our MVC framework plumbing. This is needed because in our MVC framework,
 * all views extend from JPanel, but FabricController is the controller for the
 * main application window, which is a JFrame. We could make every view extend
 * from java.awt.Container, but this would result in a lot of functionality
 * needed to support JPanels and JFrames. For only one JFrame, this approach
 * seemed more feasible.
 * 
 */
public class FabricView extends AbstractView<FabricModel, FabricController> {

    private static final long serialVersionUID = 1L;

    private final FVMainFrame mainFrame;

    public FabricView(FVMainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public IFabricView getView() {
        return mainFrame;
    }

    public FVMainFrame getMainFrame() {
        return mainFrame;
    }

    public PinBoardView getPinBoardView() {
        return mainFrame.getPinBoardView();
    }

    @Override
    public void modelUpdateFailed(FabricModel model, Throwable caught) {
        mainFrame.modelUpdateFailed(model, caught);
    }

    @Override
    public void modelChanged(FabricModel model) {
        mainFrame.modelChanged(model);
    }

    @Override
    public JComponent getMainComponent() {
        // This panel is not used at all
        return new JPanel();
    }

    @Override
    public void initComponents() {
        mainFrame.initComponents();
    }

    @Override
    public void setController(FabricController controller) {
        super.setController(controller);
        mainFrame.setController(controller);
    }
}
