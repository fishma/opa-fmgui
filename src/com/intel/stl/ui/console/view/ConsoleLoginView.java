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
 *  File Name: LoginView.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.7  2015/10/01 21:54:59  fernande
 *  Archive Log:    PR130409 - [Dell]: FMGUI Admin Console login fails when switch is configured without username and password. Removed restriction on user and password not empty
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2015/09/08 21:21:58  jijunwan
 *  Archive Log:    PR 130329 - Windows FM GUI - Admin tab - Console sidetab - need space between login buttons
 *  Archive Log:    - adjusted insets to increase space between buttons
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2015/08/17 18:54:14  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - changed frontend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2015/07/13 19:30:55  jijunwan
 *  Archive Log:    PR 129528 - input validation improvement
 *  Archive Log:    - In interactive console, the port number must be in range (0, 65535) and the password cannot be empty.
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2015/07/01 22:01:09  jijunwan
 *  Archive Log:    PR 129442 - login failed with FileNotFoundException
 *  Archive Log:    - Improved ConsoleLoginView to use full message as tooltip text
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2015/05/27 21:52:54  rjtierne
 *  Archive Log:    128874 - Eliminate login dialog from admin console and integrate into panel
 *  Archive Log:    - Renamed parentDocListener to loginListener
 *  Archive Log:    - Removed redundant call to setUI() from cboxHostName and cboxUserName which was
 *  Archive Log:    overwriting installed document listener
 *  Archive Log:    - Changed getUserName() and getHostName() to directly access editor text instead
 *  Archive Log:    of selected item, to get most updated text from JComboBox
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/05/27 14:35:03  rjtierne
 *  Archive Log:    128874 - Eliminate login dialog from admin console and integrate into panel
 *  Archive Log:    Initial Version - replaces LoginDialogView
 *  Archive Log:
 *  Archive Log:    Revision 1.15  2015/05/01 21:30:38  jijunwan
 *  Archive Log:    1) changed LoginView to DOCUMENT_MODAL
 *  Archive Log:    2) moved title border, and added title for the dialog
 *  Archive Log:
 *  Archive Log:    Revision 1.14  2015/04/29 05:41:49  fisherma
 *  Archive Log:    Fixing incorrect user name being shown in the Login Dialog on "Unlock" button click.
 *  Archive Log:
 *  Archive Log:    Revision 1.13  2015/04/09 21:14:32  rjtierne
 *  Archive Log:    Added key listener to password field to the user can hit <Enter> to launch a console
 *  Archive Log:
 *  Archive Log:    Revision 1.12  2015/04/02 13:33:06  jypak
 *  Archive Log:    Klockwork: Front End Critical Without Unit Test. 47 open issues fixed. All of them are for null checks.
 *  Archive Log:
 *  Archive Log:    Revision 1.11  2015/04/01 13:20:30  jijunwan
 *  Archive Log:    added null check
 *  Archive Log:
 *  Archive Log:    Revision 1.10  2014/10/30 17:01:01  rjtierne
 *  Archive Log:    Clear the status text area when the Ok button is pressed
 *  Archive Log:
 *  Archive Log:    Revision 1.9  2014/10/28 22:18:41  rjtierne
 *  Archive Log:    Put hideDialog() on the EDT to correct synch problem with setVisible()
 *  Archive Log:
 *  Archive Log:    Revision 1.8  2014/10/24 14:37:10  rjtierne
 *  Archive Log:    Added new conditions to existing logic to accommodate dynamic dialog usage. The
 *  Archive Log:    login dialog is now displayed at console initialization and console unlock.
 *  Archive Log:
 *  Archive Log:    Revision 1.7  2014/10/07 19:54:17  rjtierne
 *  Archive Log:    Changed constructor input parameter "owner" type from Window to IFabricView
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2014/10/01 19:58:09  rjtierne
 *  Archive Log:    Added thread to initialize console. Added UI enhancement to snap mouse to OK button on dialog.
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2014/09/23 21:18:22  rjtierne
 *  Archive Log:    Removed unecessary line of code
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2014/09/23 19:47:01  rjtierne
 *  Archive Log:    Integration of Gritty for Java Console
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/09/09 20:03:27  rjtierne
 *  Archive Log:    Added default login bean to console dialog to reduce typing
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/09/09 16:45:52  rjtierne
 *  Archive Log:    Added combo boxes for username and hostname
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/09/09 14:18:40  rjtierne
 *  Archive Log:    Initial Version
 *  Archive Log:
 *
 *  Overview: Custom dialog view for logging into a remote host
 *
 *  @author: rjtierne
 *
 ******************************************************************************/
package com.intel.stl.ui.console.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.console.IConsoleEventListener;
import com.intel.stl.ui.console.IConsoleLogin;
import com.intel.stl.ui.console.ITabListener;
import com.intel.stl.ui.console.LoginBean;

public class ConsoleLoginView extends JPanel implements IConsoleLogin {

    private static final long serialVersionUID = -8589239292130514515L;

    private JComboBox<String> cboxUserName;

    private JPasswordField txtFldPassword;

    private JComboBox<String> cboxHostName;

    private JTextField txtFldPortNum;

    private JTextArea txtAreaStatus;

    private JLabel lblStatusIcon;

    private JButton btnLogin;

    private JButton btnCancel;

    private DocumentListener setDirtyListener;

    private final IConsoleLoginListener loginListener;

    private IConsoleEventListener consoleEventListener;

    private final ITabListener tabListener;

    private LoginBean loginBean;

    private boolean newConsole;

    private int consoleId = 0;

    public ConsoleLoginView(IConsoleLoginListener parentDocListener,
            ITabListener tabListener) {

        this.loginListener = parentDocListener;
        this.tabListener = tabListener;
        createDocumentListener();
        initComponents();
    }

    protected void initComponents() {
        // Login View
        setLayout(new GridBagLayout());
        setBackground(UIConstants.INTEL_WHITE);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createTitledBorder(STLConstants.K1050_LOGIN.getValue()),
                BorderFactory.createEmptyBorder(5, 2, 5, 2)));

        // Gridbag Constraints
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(1, 10, 3, 2);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.ipadx = 10;

        // JLabel: User Name
        gc.gridx = 0;
        gc.gridy = 0;
        JLabel lblUserName =
                ComponentFactory.getH5Label(
                        STLConstants.K0602_USER_NAME.getValue() + ": ",
                        Font.BOLD);
        lblUserName.setHorizontalAlignment(JLabel.RIGHT);
        add(lblUserName, gc);

        // JComboBox: User Name
        gc.insets = new Insets(1, 2, 3, 2);
        gc.gridx++;
        gc.gridy = 0;
        cboxUserName =
                ComponentFactory.createComboBox(new String[] {},
                        setDirtyListener);
        cboxUserName.setEditable(true);
        add(cboxUserName, gc);

        // JLabel: Host Name
        gc.insets = new Insets(1, 3, 3, 2);
        gc.gridx++;
        gc.gridy = 0;
        JLabel lblHostName =
                ComponentFactory.getH5Label(STLConstants.K0051_HOST.getValue()
                        + ": ", Font.BOLD);
        lblHostName.setHorizontalAlignment(JLabel.RIGHT);
        add(lblHostName, gc);

        // JComboBox: Host Name
        gc.insets = new Insets(1, 2, 3, 2);
        gc.gridx++;
        gc.gridy = 0;
        cboxHostName =
                ComponentFactory.createComboBox(new String[] {},
                        setDirtyListener);
        cboxHostName.setEditable(true);
        add(cboxHostName, gc);

        // JButton: Login
        gc.insets = new Insets(1, 15, 3, 2);
        gc.gridx++;
        gc.gridy = 0;
        btnLogin =
                ComponentFactory.getIntelActionButton(STLConstants.K1050_LOGIN
                        .getValue());
        btnLogin.setEnabled(true);
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLogin();
            }
        });
        add(btnLogin, gc);

        // JLabel: Password
        gc.insets = new Insets(2, 10, 2, 2);
        gc.gridx = 0;
        gc.gridy = 1;
        JLabel lblPasswordName =
                ComponentFactory.getH5Label(
                        STLConstants.K1049_PASSWORD.getValue() + ": ",
                        Font.BOLD);
        lblPasswordName.setHorizontalAlignment(JLabel.RIGHT);
        add(lblPasswordName, gc);

        // JPasswordField: Password
        gc.insets = new Insets(2, 2, 2, 2);
        gc.gridx++;
        gc.gridy = 1;
        txtFldPassword = ComponentFactory.createPasswordField(setDirtyListener);
        txtFldPassword.setInputVerifier(null);
        txtFldPassword.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    onLogin();
                }
            }
        });
        add(txtFldPassword, gc);

        // JLabel: Port #
        gc.insets = new Insets(2, 3, 2, 2);
        gc.gridx++;
        gc.gridy = 1;
        JLabel lblPortNum =
                ComponentFactory
                        .getH5Label(
                                STLConstants.K1035_CONFIGURATION_PORT
                                        .getValue() + ": ", Font.BOLD);
        lblPortNum.setHorizontalAlignment(JLabel.RIGHT);
        add(lblPortNum, gc);

        // JTextField: Port #
        gc.insets = new Insets(2, 2, 2, 2);
        gc.gridx++;
        gc.gridy = 1;
        txtFldPortNum =
                ComponentFactory
                        .createNumericTextField(65535, setDirtyListener);
        add(txtFldPortNum, gc);

        // JButton: Cancel
        gc.insets = new Insets(2, 15, 2, 2);
        gc.gridx++;
        gc.gridy = 1;
        btnCancel =
                ComponentFactory.getIntelActionButton(STLConstants.K0621_CANCEL
                        .getValue());
        btnCancel.setEnabled(true);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (newConsole) {
                    tabListener.closeConsole(tabListener.getCurrentTabView());
                }
                killProgress();
                hideLogin();
                setDirty();
                loginListener.enableLock(false);
                setVisible(false);
            }
        });
        add(btnCancel, gc);

        // Status Panel (Status Icon)
        gc.insets = new Insets(2, 10, 2, 2);
        gc.gridx = 0;
        gc.gridy = 2;
        lblStatusIcon = new JLabel();
        lblStatusIcon.setIcon(UIImages.RUNNING.getImageIcon());
        lblStatusIcon.setVisible(false);
        add(lblStatusIcon, gc);

        // Status Panel (Text Area)
        gc.insets = new Insets(2, 2, 2, 2);
        gc.gridy = 2;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        txtAreaStatus = new JTextArea();
        txtAreaStatus.setOpaque(true);
        txtAreaStatus.setEditable(false);
        txtAreaStatus.setFont(UIConstants.H5_FONT);
        txtAreaStatus.setForeground(UIConstants.INTEL_DARK_GRAY);
        txtAreaStatus.setBackground(UIConstants.INTEL_BACKGROUND_GRAY);
        txtAreaStatus.setPreferredSize(new Dimension(1, 20));
        add(txtAreaStatus, gc);
    }

    public void createDocumentListener() {

        setDirtyListener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                setDirty();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setDirty();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setDirty();
            }
        };
    }

    protected void onLogin() {

        // Clear the status info
        txtAreaStatus.setText("");
        txtAreaStatus.setToolTipText(null);

        LoginBean loginBean = new LoginBean();
        loginBean.setUserName(getUserName());
        loginBean.setPassword(getPassword());
        loginBean.setHostName(getHostName());
        loginBean.setPortNum(getPortNum());

        startProgress();

        if (newConsole) {
            int id = consoleId;
            consoleEventListener.initializeConsoleThread(id, loginBean, null);
            addCBoxItem(getHostName(), cboxHostName);
        } else {
            if (consoleId > 0) {
                consoleEventListener.onUnlockThread(consoleId, getPassword());
            }
        }
    }

    public void addCBoxItem(String item, JComboBox<String> cbox) {
        if (item == null) {
            return;
        }

        for (int i = 0; i < cbox.getItemCount(); i++) {
            String history = cbox.getItemAt(i);
            if (item.equals(history)) {
                cbox.setSelectedIndex(i);
                return;
            }
        }
        cbox.addItem(item);
    }

    /**
     * @return the loginBean
     */
    public LoginBean getLoginBean() {
        return loginBean;
    }

    /**
     * @param loginBean
     *            the loginBean to set
     */
    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    @Override
    public void showLogin(final LoginBean loginBean) {

        if (loginBean != null) {
            setLoginBean(loginBean);

            // Populate the fields with the default login information
            addCBoxItem(loginBean.getHostName(), cboxHostName);
            txtFldPortNum.setText(loginBean.getPortNum());
        }

        // Enable the OK button
        if (getUserName().length() == 0) {
            cboxUserName.requestFocus();
        } else if (getPassword().length == 0) {
            txtFldPassword.requestFocus();
        } else if (getHostName().length() == 0) {
            cboxHostName.requestFocus();
        } else if (getPortNum().length() == 0) {
            txtFldPortNum.requestFocus();
        }
        setVisible(true);
        setDirty();
    }

    @Override
    public void showLogin(LoginBean loginBean, boolean newConsole, int consoleId) {

        this.newConsole = newConsole;
        this.consoleId = consoleId;

        // Set only password field enabled if this is an existing
        // console that was locked
        if (newConsole) {
            cboxUserName.setEnabled(true);
            txtFldPassword.setEnabled(true);
            cboxHostName.setEnabled(true);
            txtFldPortNum.setEnabled(true);
        } else {
            cboxUserName.setEnabled(false);
            txtFldPassword.setEnabled(true);
            cboxHostName.setEnabled(false);
            txtFldPortNum.setEnabled(false);
        }

        showLogin(loginBean);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.console.IConsoleLogin#hideDialog()
     */
    @Override
    public void hideLogin() {

        Runnable hideIt = new Runnable() {

            @Override
            public void run() {
                txtFldPassword.getDocument().removeDocumentListener(
                        setDirtyListener);
                txtFldPassword.setText("");
                txtFldPassword.getDocument().addDocumentListener(
                        setDirtyListener);

                if (isVisible()) {
                    setVisible(false);
                }
                // setDirty();
            }
        };
        Util.runInEDT(hideIt);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.console.IConsoleLogin#showMessage(java.lang.String)
     */
    @Override
    public void showMessage(String message) {
        txtAreaStatus.setText(message);
        txtAreaStatus.setToolTipText(message);
    }

    protected void setDirty() {

        killProgress();

        if (txtAreaStatus != null) {
            showMessage(null);
        }

        /*-
         * PRR switches can be configure thru command loginmode to not require a user
         * and password. We are now allowing users to login without specifying either
         * of them 
         * if ((getUserName().length() > 0) && (getPassword().length > 0)
         *         && (getHostName().length() > 0) && (getPortNum().length() > 0)) {
         */
        if ((getHostName().length() > 0) && (getPortNum().length() > 0)) {
            btnLogin.setEnabled(true);
        } else {
            btnLogin.setEnabled(false);
        }

        // Update the ConsoleTerminalView
        loginListener.updateUIComponents(btnLogin.isEnabled());

    }

    @Override
    public void startProgress() {
        lblStatusIcon.setVisible(true);
    }

    @Override
    public void killProgress() {
        lblStatusIcon.setVisible(false);
    }

    @Override
    public String getUserName() {
        JTextComponent tc =
                (JTextComponent) cboxUserName.getEditor().getEditorComponent();
        return tc.getText();
    }

    @Override
    public char[] getPassword() {
        return txtFldPassword.getPassword();
    }

    @Override
    public String getHostName() {
        JTextComponent tc =
                (JTextComponent) cboxHostName.getEditor().getEditorComponent();
        return tc.getText();
    }

    @Override
    public String getPortNum() {
        return txtFldPortNum.getText();
    }

    @Override
    public void setConsoleEventListener(
            IConsoleEventListener consoleEventListener) {
        this.consoleEventListener = consoleEventListener;
    }

}
