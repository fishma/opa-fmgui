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
 *  File Name: CertDescription.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.6.2.1  2015/05/06 19:20:30  jijunwan
 *  Archive Log:    fixed Serializable issue found by FindBug
 *  Archive Log:
 *  Archive Log:    Revision 1.8  2015/05/04 22:25:39  jijunwan
 *  Archive Log:    made password to be transient, also updated orm.xml
 *  Archive Log:
 *  Archive Log:    Revision 1.7  2015/05/04 22:22:32  jijunwan
 *  Archive Log:    set CertsDescription to be transient by mistake. Change it back now, and make it to be Serializable since HostInfo is Serializable
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2015/04/08 15:16:19  fernande
 *  Archive Log:    Changes to allow for failover to work when the current (initial) FE is not available.
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2015/03/27 22:42:22  fernande
 *  Archive Log:    Changed back password fields from PasswordField to char[]
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2015/03/27 20:30:11  fernande
 *  Archive Log:    Adding support for failover. The UI can now setup objects to listen to SubnetEvents the the ISubnetEventListener. Listener register in the SubnetContext
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2015/03/16 17:33:34  fernande
 *  Archive Log:    STLConnection lifecycle support. STLConnections can now be reused and temporary connections are not cached and their socket is closed after they are logically closed. Changed SubnetDescription in support of failover to have a list of HostInfo objects instead of just info for one host.
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2015/02/09 21:28:43  jijunwan
 *  Archive Log:    fixed a bug
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/01/11 20:56:32  jijunwan
 *  Archive Log:    support secure FE:
 *  Archive Log:    1) added secured STL Connection to communicate with FE
 *  Archive Log:    2) added cert assistant interface that supports certs conf, persistence and password prompt
 *  Archive Log:    3) added default cert assistant
 *  Archive Log:    4) improved Subnet conf to support secure FE
 *  Archive Log:
 *  Archive Log:    NOTE: the secured connection requires Java 1.7
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.api;

import java.io.Serializable;

public class CertsDescription implements Serializable {
    private static final long serialVersionUID = 1923500609559782312L;

    private String keyStoreFile;

    private String trustStoreFile;

    private transient char[] keyStorePwd;

    private transient char[] trustStorePwd;

    public CertsDescription() {
    }

    /**
     * Description:
     * 
     * @param keyStoreFile
     * @param trustStoreFile
     */
    public CertsDescription(String keyStoreFile, String trustStoreFile) {
        this.keyStoreFile = keyStoreFile;
        this.trustStoreFile = trustStoreFile;
    }

    public void setKeyStore(String file, char[] pwd) {
        keyStoreFile = file;
        keyStorePwd = pwd;
    }

    public void setTrustStore(String file, char[] pwd) {
        trustStoreFile = file;
        trustStorePwd = pwd;
    }

    public void setKeyStoreFile(String keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }

    /**
     * @return the keyStoreFile
     */
    public String getKeyStoreFile() {
        return keyStoreFile;
    }

    /**
     * @param keyStorePwd
     *            the keyStorePwd to set
     */
    public void setKeyStorePwd(char[] keyStorePwd) {
        this.keyStorePwd = keyStorePwd;
    }

    /**
     * @return the keyStorePwd
     */
    public char[] getKeyStorePwd() {
        return keyStorePwd;
    }

    public void setTrustStoreFile(String trustStoreFile) {
        this.trustStoreFile = trustStoreFile;
    }

    /**
     * @return the trustStoreFile
     */
    public String getTrustStoreFile() {
        return trustStoreFile;
    }

    /**
     * @param trustStorePwd
     *            the trustStorePwd to set
     */
    public void setTrustStorePwd(char[] trustStorePwd) {
        this.trustStorePwd = trustStorePwd;
    }

    /**
     * @return the trustStorePwd
     */
    public char[] getTrustStorePwd() {
        return trustStorePwd;
    }

    public void clearKeyPwd() {
        clearPwd(keyStorePwd);
        keyStorePwd = null;
    }

    public void clearTrustPwd() {
        clearPwd(trustStorePwd);
        trustStorePwd = null;
    }

    public void clearPwds() {
        clearKeyPwd();
        clearTrustPwd();
    }

    public boolean hasPwds() {
        boolean hasPwds =
                keyStorePwd != null && keyStorePwd.length > 0
                        && trustStorePwd != null && trustStorePwd.length > 0;
        return hasPwds;
    }

    private void clearPwd(char[] pwd) {
        if (pwd != null) {
            for (int i = 0; i < pwd.length; i++) {
                pwd[i] = '\0';
            }
        }
    }

    /**
     * <i>Description:</i>
     * 
     * @return
     */
    public boolean isEmpty() {
        return keyStoreFile == null || keyStoreFile.isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
                prime
                        * result
                        + ((keyStoreFile == null) ? 0 : keyStoreFile.hashCode());
        result =
                prime
                        * result
                        + ((trustStoreFile == null) ? 0 : trustStoreFile
                                .hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        CertsDescription other = (CertsDescription) obj;
        if (keyStoreFile == null) {
            if (other.keyStoreFile != null) {
                return false;
            }
        } else if (!keyStoreFile.equals(other.keyStoreFile)) {
            return false;
        }
        if (trustStoreFile == null) {
            if (other.trustStoreFile != null) {
                return false;
            }
        } else if (!trustStoreFile.equals(other.trustStoreFile)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CertsDescription [keyStoreFile=" + keyStoreFile
                + ", trustStoreFile=" + trustStoreFile + "]";
    }

}