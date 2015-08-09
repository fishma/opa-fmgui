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
 *  File Name: DevicegroupSelectionPanel.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.1  2015/03/24 17:46:09  jijunwan
 *  Archive Log:    init version of DeviceGroup editor
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.ui.admin.view.devicegroups;

import java.awt.Font;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import com.intel.stl.ui.admin.impl.devicegroups.IResourceSelector;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.IntelTabbedPaneUI;

public class DevicegroupSelectionPanel extends JTabbedPane {
    private static final long serialVersionUID = 3325634575575732829L;

    public DevicegroupSelectionPanel() {
        super();
        initComponent();
    }

    protected void initComponent() {
        setBackground(UIConstants.INTEL_WHITE);
        setBorder(BorderFactory
                .createTitledBorder(STLConstants.K2133_RESOURCES_SELECTION
                        .getValue()));
        IntelTabbedPaneUI tabUi = new IntelTabbedPaneUI();
        setUI(tabUi);
        tabUi.setFont(UIConstants.H5_FONT.deriveFont(Font.BOLD));
    }

    public void setSelectors(Collection<IResourceSelector> selectors) {
        for (IResourceSelector selector : selectors) {
            addTab(selector.getName(), selector.getView());
        }
    }

    public String getSelectorName() {
        int index = getSelectedIndex();
        String name = getTitleAt(index);
        return name;
    }

}