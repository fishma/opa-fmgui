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

package com.intel.stl.api.configuration;

import java.io.Serializable;
import java.util.List;

/*******************************************************************************
 * I N T E L C O R P O R A T I O N
 * 
 * Functional Group: Fabric Viewer Application
 * 
 * File Name: LoggingConfiguration.java
 * 
 * Archive Source: $Source:
 * /cvs/vendor/intel/fmgui/server/src/main/java/com/intel
 * /stl/api/configuration/LoggingConfiguration.java,v $
 * 
 * Archive Log: $Log$
 * Archive Log: Revision 1.3  2014/05/13 18:31:47  fernande
 * Archive Log: Implemented saveLoggingConfiguration and getLoggingConfiguration
 * Archive Log: Archive Log: Revision 1.2
 * 2014/04/19 14:31:34 fernande Archive Log: More changes to plug the Setup
 * wizard Archive Log: Archive Log: Revision 1.1 2014/04/18 18:27:05 jypak
 * Archive Log: Config API for Setup Wizard Migration. Archive Log: Archive Log:
 * Revision 1.1 2014/04/12 20:39:17 fernande Archive Log: Initial version
 * Archive Log: Archive Log: Revision 1.1 2014/04/09 16:35:11 jypak Archive Log:
 * Setup Wizard. The SetupWizardMain class control the wizard to come up as an
 * initial or a non-initial setup wizard. Once the Setup Wizard is incorporated
 * to the whole GUI, the main class should be discarded. Archive Log:
 * 
 * Overview: A bean to be transferred between the Config API
 * (LoggingConfigurationController) and SetupWizard for logging configuration
 * info.
 * 
 * @author jypak
 * 
 ******************************************************************************/
public class LoggingConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<AppenderConfig> appenders = null;

    /**
     * @return the list of appenders
     */
    public List<AppenderConfig> getAppenders() {
        return appenders;
    }

    /**
     * @param appenders
     *            the list of appenders to set
     */
    public void setAppenders(List<AppenderConfig> appenders) {
        this.appenders = appenders;
    }

}