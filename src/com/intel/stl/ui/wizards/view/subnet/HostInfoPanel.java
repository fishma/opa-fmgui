/**
 * INTEL CONFIDENTIAL
 * Copyright (c) 2015 Intel Corporation All Rights Reserved.
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
 *  File Name: HostInfoPanel.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.12  2015/04/29 19:14:42  rjtierne
 *  Archive Log:    Fixed typo
 *  Archive Log:
 *  Archive Log:    Revision 1.11  2015/04/21 21:18:50  rjtierne
 *  Archive Log:    In method setDirty(), calling setDirty() in SubnetwizardView to enable the Apply and Reset
 *  Archive Log:    buttons only when the fields change
 *  Archive Log:
 *  Archive Log:    Revision 1.10  2015/04/20 15:22:18  rjtierne
 *  Archive Log:    Initialize port number to default when creating new hosts within a subnet
 *  Archive Log:
 *  Archive Log:    Revision 1.9  2015/04/09 21:14:44  rjtierne
 *  Archive Log:    In method testConnection(), prevent running a test on a host panel having no host name or port number
 *  Archive Log:
 *  Archive Log:    Revision 1.8  2015/04/07 20:32:27  jijunwan
 *  Archive Log:    added null check
 *  Archive Log:
 *  Archive Log:    Revision 1.7  2015/04/07 20:17:42  jijunwan
 *  Archive Log:    second round wizard polishment
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2015/04/06 22:53:48  jijunwan
 *  Archive Log:    first round wizard polishment
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2015/04/03 14:44:37  rjtierne
 *  Archive Log:    Added showFileChooser() method to center file browser over the wizard window
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2015/04/01 17:05:45  rjtierne
 *  Archive Log:    Added color to results when testing connection on host panels.
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2015/03/30 15:13:18  rjtierne
 *  Archive Log:    Reorganized view to hold the connection test panel and remove the master radio button
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2015/03/25 17:56:57  rjtierne
 *  Archive Log:    Move JFileChooser to the subnet wizard view level so previously traversed
 *  Archive Log:    directory locations are remembered
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/03/20 21:07:59  rjtierne
 *  Archive Log:    Initial Version
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: rjtierne
 *
 ******************************************************************************/

package com.intel.stl.ui.wizards.view.subnet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.view.ComponentFactory;

public class HostInfoPanel extends JPanel {

    private static final long serialVersionUID = -7241402810197918958L;

    private final IHostInfoListener hostInfoListener;

    private final HostInfoPanel hostInfoPanel = this;

    private boolean currentMaster;

    private boolean dirty;

    private JPanel hostPanel;

    private JButton btnRemove;

    private JTextField txtFldHostName;

    private JTextField txtFldPortNum;

    private JCheckBox chkboxSecureConnect;

    private JTextField txtFldKeyStoreFile;

    private JButton btnKeyStoreBrowser;

    private JTextField txtFldTrustStoreFile;

    private JButton btnTrustStoreBrowser;

    private JPanel pnlConnection;

    private JPanel pnlSecurity;

    private JPanel pnlHostEntry;

    private final JFileChooser chooser;

    private JLabel lblConnectionStatus;

    private JButton btnConnectionTest;

    private final HostInfoPanel thisPanel = this;

    private final Insets insets = new Insets(3, 2, 3, 2);

    private final Insets widthInsets = new Insets(3, 10, 3, 2);

    public HostInfoPanel(IHostInfoListener hostInfoListener,
            JFileChooser chooser) {
        this.hostInfoListener = hostInfoListener;
        this.chooser = chooser;
        initComponents();
        enableCerts(false);
    }

    protected void initComponents() {
        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createMatteBorder(1, 1, 1, 4, UIConstants.INTEL_LIGHT_GRAY),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));

        add(getHostPanel(), BorderLayout.CENTER);

        // Remove Button
        btnRemove =
                ComponentFactory.getImageButton(UIImages.CLOSE_RED
                        .getImageIcon());
        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hostInfoListener.removeHost(hostInfoPanel);
            }
        });
        add(btnRemove, BorderLayout.EAST);
    }

    protected JPanel getHostPanel() {
        if (hostPanel == null) {
            hostPanel = new JPanel(new GridBagLayout());
            hostPanel.setBackground(UIConstants.INTEL_WHITE);
            hostPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 2, 5));

            GridBagConstraints gc = new GridBagConstraints();
            gc.fill = GridBagConstraints.BOTH;

            gc.gridwidth = 1;
            gc.insets = new Insets(7, 2, 3, 2);
            JLabel label =
                    ComponentFactory.getH5Label(
                            STLConstants.K3037_FE_CONNECTION.getValue(),
                            Font.BOLD);
            label.setHorizontalAlignment(JLabel.TRAILING);
            label.setVerticalAlignment(JLabel.TOP);
            hostPanel.add(label, gc);

            gc.weightx = 1;
            gc.gridwidth = GridBagConstraints.REMAINDER;
            gc.insets = widthInsets;
            JPanel panel = getConnectionPanel();
            hostPanel.add(panel, gc);

            gc.weightx = 0;
            gc.gridwidth = 1;
            gc.insets = insets;
            label =
                    ComponentFactory.getH5Label(
                            STLConstants.K3033_CONNECTION_TEST.getValue(),
                            Font.BOLD);
            label.setHorizontalAlignment(JLabel.TRAILING);
            hostPanel.add(label, gc);

            gc.weightx = 1;
            gc.gridwidth = GridBagConstraints.REMAINDER;
            gc.insets = widthInsets;
            panel = getConnTestPanel();
            hostPanel.add(panel, gc);
        }
        return hostPanel;
    }

    protected JPanel getConnectionPanel() {
        if (pnlConnection == null) {
            pnlConnection = new JPanel(new BorderLayout(5, 5));
            pnlConnection.setOpaque(false);
            pnlConnection.add(getHostEntryPanel(), BorderLayout.NORTH);
            pnlConnection.add(getSecurityPanel(), BorderLayout.CENTER);
        }
        return pnlConnection;
    }

    protected JPanel getHostEntryPanel() {
        if (pnlHostEntry == null) {
            pnlHostEntry = new JPanel(new BorderLayout(5, 5));
            pnlHostEntry.setOpaque(false);

            JPanel panel = new JPanel(new GridLayout(1, 2, 10, 5));
            panel.setOpaque(false);
            FieldPair fp =
                    new FieldPair(STLConstants.K0051_HOST.getValue(), false);
            txtFldHostName = fp.getTxtFld();
            panel.add(fp);

            fp =
                    new FieldPair(
                            STLConstants.K1035_CONFIGURATION_PORT.getValue(),
                            true);
            txtFldPortNum = fp.getTxtFld();
            txtFldPortNum.setText(STLConstants.K3015_DEFAULT_PORT.getValue());
            panel.add(fp);
            pnlHostEntry.add(panel, BorderLayout.CENTER);

            chkboxSecureConnect =
                    ComponentFactory
                            .getIntelCheckBox(STLConstants.K2003_SECURE_CONNECT
                                    .getValue());
            chkboxSecureConnect.setFont(UIConstants.H5_FONT);
            chkboxSecureConnect.setForeground(UIConstants.INTEL_DARK_GRAY);
            chkboxSecureConnect.setHorizontalAlignment(JLabel.TRAILING);
            chkboxSecureConnect.setSelected(false);
            chkboxSecureConnect.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    dirty = true;
                    hostInfoListener.setDirty();
                    boolean isSecureConnect = chkboxSecureConnect.isSelected();
                    enableCerts(isSecureConnect);
                }
            });
            pnlHostEntry.add(chkboxSecureConnect, BorderLayout.EAST);
        }
        return pnlHostEntry;
    }

    protected JPanel getSecurityPanel() {
        if (pnlSecurity == null) {
            pnlSecurity = new JPanel(new GridLayout(1, 2, 10, 5));
            pnlSecurity.setOpaque(false);
            pnlSecurity.setBorder(BorderFactory
                    .createTitledBorder(STLConstants.K3041_SSL.getValue()));

            SecureStorage ss =
                    new SecureStorage(STLConstants.K2001_KEY_STORE.getValue());
            txtFldKeyStoreFile = ss.getTxtFld();
            btnKeyStoreBrowser = ss.getBtnStoreBrowser();
            pnlSecurity.add(ss);

            ss = new SecureStorage(STLConstants.K2002_TRUST_STORE.getValue());
            txtFldTrustStoreFile = ss.getTxtFld();
            btnTrustStoreBrowser = ss.getBtnStoreBrowser();
            pnlSecurity.add(ss);
        }
        return pnlSecurity;
    }

    protected JPanel getConnTestPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = insets;

        gc.gridwidth = 1;
        btnConnectionTest =
                ComponentFactory.getImageButton(UIImages.PLAY.getImageIcon());
        btnConnectionTest.setToolTipText(STLConstants.K3027_TEST_CONNECTION
                .getValue());
        btnConnectionTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnConnectionTest.isSelected()) {
                    stopConnectionTest();
                } else {
                    testConnection();
                }
            }
        });
        panel.add(btnConnectionTest, gc);

        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        lblConnectionStatus =
                ComponentFactory.getH5Label(
                        STLConstants.K3028_NOT_TESTED.getValue(), Font.PLAIN);
        panel.add(lblConnectionStatus, gc);

        return panel;
    }

    protected void testConnection() {

        if (txtFldHostName.getText().equals("")
                || txtFldPortNum.getText().equals("")) {
            hostInfoListener
                    .showErrorMessage(STLConstants.K3042_HOST_PORT_BLANK
                            .getValue());
            return;
        }

        btnConnectionTest.setSelected(true);

        if (hostInfoListener.hasDuplicateHosts()) {
            hostInfoListener.showErrorMessage(UILabels.STL50086_DUPLICATE_HOSTS
                    .getDescription());
            return;
        }

        btnConnectionTest.setIcon(UIImages.STOP.getImageIcon());
        lblConnectionStatus.setIcon(UIImages.RUNNING.getImageIcon());
        setLabel(lblConnectionStatus, STLConstants.K0606_CONNECTING.getValue(),
                UIConstants.INTEL_DARK_GRAY);
        hostInfoListener.runConnectionTest(thisPanel);
    }

    public void enableRemove(boolean enable) {
        btnRemove.setEnabled(enable);
    }

    public void setFocus() {
        txtFldHostName.grabFocus();
    }

    public void stopConnectionTest() {
        btnConnectionTest.setIcon(UIImages.PLAY.getImageIcon());
        lblConnectionStatus.setIcon(null);
        setLabel(lblConnectionStatus, STLConstants.K3028_NOT_TESTED.getValue(),
                UIConstants.INTEL_DARK_GRAY);
        // TODO cancel task...
        btnConnectionTest.setSelected(false);
    }

    public void setConnectionStatus(String status) {
        btnConnectionTest.setIcon(UIImages.PLAY.getImageIcon());
        btnConnectionTest.setSelected(false);
        lblConnectionStatus.setIcon(null);
        Color color =
                (status.equals(STLConstants.K3031_PASS.getValue())) ? UIConstants.DRAK_GREEN
                        : UIConstants.INTEL_RED;
        setLabel(lblConnectionStatus, status, color);
        btnConnectionTest.setIcon(UIImages.PLAY.getImageIcon());
    }

    protected void setLabel(JLabel lbl, String value, Color color) {
        lbl.setText(value);
        lbl.setForeground(color);
    }

    protected void enableCerts(boolean enable) {
        txtFldKeyStoreFile.setEnabled(enable);
        if (btnKeyStoreBrowser != null) {
            btnKeyStoreBrowser.setEnabled(enable);
        }
        txtFldTrustStoreFile.setEnabled(enable);
        if (btnTrustStoreBrowser != null) {
            btnTrustStoreBrowser.setEnabled(enable);
        }

        Color color = (enable) ? UIConstants.INTEL_WHITE : null;
        txtFldKeyStoreFile.setBackground(color);
        txtFldTrustStoreFile.setBackground(color);

        pnlSecurity.setVisible(enable);
    }

    public void setCurrentMaster(boolean currentMaster) {
        this.currentMaster = currentMaster;
    }

    public boolean isCurrentMaster() {
        return currentMaster;
    }

    public void setHostName(String hostName) {
        txtFldHostName.setText(hostName);
    }

    public String getHostName() {
        return txtFldHostName.getText();
    }

    public void setPortNum(String portNum) {
        txtFldPortNum.setText(portNum);
    }

    public String getPortNum() {
        return txtFldPortNum.getText();
    }

    public void setSecureConnection(boolean b) {
        chkboxSecureConnect.setSelected(b);
    }

    public boolean isSecureConnection() {
        return chkboxSecureConnect.isSelected();
    }

    public void setKeyStoreFile(String keyStoreFileName) {
        txtFldKeyStoreFile.setText(keyStoreFileName);
    }

    public String getKeyStoreFile() {
        return txtFldKeyStoreFile.getText();
    }

    public void setTrustFileFile(String trustStoreFileName) {
        txtFldTrustStoreFile.setText(trustStoreFileName);
    }

    public String getTrustStoreFile() {
        return txtFldTrustStoreFile.getText();
    }

    protected void setDirty() {
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        hostInfoListener.setDirty(dirty);
    }

    public boolean isHostNamePopulated() {
        return (txtFldHostName.getText().length() > 0);
    }

    public boolean isPortNumPopulated() {
        return (txtFldPortNum.getText().length() > 0);
    }

    public boolean isKeyStorePopulated() {
        return (txtFldKeyStoreFile.getText().length() > 0);
    }

    public boolean isTrustStorePopulated() {
        return (txtFldTrustStoreFile.getText().length() > 0);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final HostInfoPanel other = (HostInfoPanel) obj;
        if (txtFldHostName.getText() == null) {
            if (other.txtFldHostName.getText() != null) {
                return false;
            }
        } else if (!txtFldHostName.getText().equalsIgnoreCase(
                other.txtFldHostName.getText())) {
            return false;
        }

        if (txtFldPortNum.getText() == null) {
            if (other.txtFldPortNum.getText() != null) {
                return false;
            }
        } else if (!txtFldPortNum.getText().equalsIgnoreCase(
                other.txtFldPortNum.getText())) {
            return false;
        }

        if (txtFldKeyStoreFile.getText() == null) {
            if (other.txtFldKeyStoreFile.getText() != null) {
                return false;
            }
        } else if (!txtFldKeyStoreFile.getText().equalsIgnoreCase(
                other.txtFldKeyStoreFile.getText())) {
            return false;
        }

        if (txtFldTrustStoreFile.getText() == null) {
            if (other.txtFldTrustStoreFile.getText() != null) {
                return false;
            }
        } else if (!txtFldTrustStoreFile.getText().equalsIgnoreCase(
                other.txtFldTrustStoreFile.getText())) {
            return false;
        }

        if (chkboxSecureConnect == null) {
            if (other.chkboxSecureConnect != null) {
                return false;
            }
        } else if ((chkboxSecureConnect != null)
                && (other.chkboxSecureConnect != null)) {
            boolean case1 =
                    !chkboxSecureConnect.isSelected()
                            && other.isSecureConnection();
            boolean case2 =
                    chkboxSecureConnect.isSelected()
                            && !other.isSecureConnection();
            if (case1 || case2) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;

        try {
            int prime = 13;

            result =
                    prime
                            * result
                            + ((getHostName() == null) ? 0 : getHostName()
                                    .toLowerCase().hashCode());
            result =
                    prime
                            * result
                            + ((getHostName().equals(null)) ? 0 : getHostName()
                                    .hashCode());

            result = prime * result + Integer.valueOf(getPortNum());
            result =
                    prime
                            * result
                            + +((getKeyStoreFile().equals(null)) ? 0
                                    : getKeyStoreFile().hashCode());
            result =
                    prime
                            * result
                            + +((getTrustStoreFile().equals(null)) ? 0
                                    : getTrustStoreFile().hashCode());

        } catch (NumberFormatException e) {
            hostInfoListener.showErrorMessage("Hash Code Error: "
                    + this.getClass().getName());
        }

        return result;
    }

    protected class FieldPair extends JPanel {
        private static final long serialVersionUID = 2171493826241135628L;

        protected JLabel label;

        protected JTextField textField;

        public FieldPair(String name, boolean isNumeric) {
            initComponents(name, isNumeric);
        }

        protected void initComponents(String name, boolean isNumeric) {
            setLayout(new BorderLayout());
            setOpaque(false);

            // Create a label
            label = createLabel(name);
            add(label, BorderLayout.WEST);

            textField = crateTextField(isNumeric);
            add(textField, BorderLayout.CENTER);
        }

        protected JLabel createLabel(String name) {
            JLabel lblName =
                    ComponentFactory.getH6Label(name + ": ", Font.PLAIN);
            lblName.setHorizontalAlignment(JLabel.LEFT);
            return lblName;
        }

        public JLabel getLabel() {
            return label;
        }

        protected JTextField crateTextField(boolean isNumeric) {
            return (isNumeric) ? ComponentFactory
                    .createNumericTextField(hostInfoListener
                            .getDocumentListeners()) : ComponentFactory
                    .createTextField(hostInfoListener.getDocumentListeners());
        }

        public JTextField getTxtFld() {
            return textField;
        }
    } // class FieldPair

    protected class SecureStorage extends FieldPair {
        private static final long serialVersionUID = -3358500581251310645L;

        private JButton btnStoreBrowser;

        public SecureStorage(String name) {
            super(name, false);
        }

        @Override
        protected void initComponents(String name, boolean isNumeric) {
            super.initComponents(name, isNumeric);

            btnStoreBrowser =
                    ComponentFactory.getImageButton(UIImages.FOLDER_ICON
                            .getImageIcon());
            btnStoreBrowser.setMargin(new Insets(2, 2, 2, 2));
            btnStoreBrowser
                    .setToolTipText(STLConstants.K0642_BROWSE.getValue());
            btnStoreBrowser.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String newFile = chooseFile(textField.getText());
                    if (newFile != null) {
                        textField.setText(newFile);
                    }
                }
            });
            add(btnStoreBrowser, BorderLayout.EAST);
        }

        protected String chooseFile(String iniFile) {

            if (!iniFile.isEmpty()) {
                File file = new File(iniFile);
                chooser.setCurrentDirectory(file.getParentFile());
            }
            int returnVal = hostInfoListener.showFileChooser();
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                return chooser.getSelectedFile().getAbsolutePath();
            }
            return null;
        }

        public JButton getBtnStoreBrowser() {
            return btnStoreBrowser;
        }

    }
}