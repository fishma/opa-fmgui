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
 *  File Name: DGAttrPanel.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.3  2015/03/30 14:25:38  jijunwan
 *  Archive Log:    1) introduced IRendererModel to create renderer only we nee
 *  Archive Log:    2) removed #getName from IAttrRenderer to provide more flexibilities and let IRendererModel to take care which attribute should use which renderer, how to init it properly
 *  Archive Log:    3) improved to support repeatable and non-repeatable attributes. For non-repeatable attributes, we only can add once
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2015/03/24 17:46:09  jijunwan
 *  Archive Log:    init version of DeviceGroup editor
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/03/16 22:08:14  jijunwan
 *  Archive Log:    added device group visualization on UI
 *  Archive Log:    some refactory to make the conf framework to be more general
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.ui.admin.view.devicegroups;

import com.intel.stl.api.management.IAttribute;
import com.intel.stl.ui.admin.impl.devicegroups.DeviceGroupRendererModel;
import com.intel.stl.ui.admin.view.AbstractAttrPanel;
import com.intel.stl.ui.admin.view.IAttrRenderer;

public class DevicegroupAttrPanel extends AbstractAttrPanel {
    private static final long serialVersionUID = 2706625376328389408L;

    private final DevicegroupsEditorPanel parent;

    public DevicegroupAttrPanel(DevicegroupsEditorPanel parent,
            DeviceGroupRendererModel rendererModel) {
        super(rendererModel);
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.admin.view.AbstractAttrPanel#setAttr(java.lang.String,
     * com.intel.stl.api.management.IAttribute, boolean)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E extends IAttribute> void setAttr(String type, E attr,
            boolean isEditable) {
        super.setAttr(type, attr, isEditable);
        typeList.setEnabled(false);
        IAttrRenderer<E> renderer = (IAttrRenderer<E>) getAttrRenderer();
        if (renderer != null) {
            renderer.setEditable(false);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.view.AbstractAttrPanel#onAddAttr()
     */
    @Override
    protected void onAddAttr() {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.view.AbstractAttrPanel#onRemoveAttr()
     */
    @Override
    protected void onRemoveAttr() {
        parent.removeEditor(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.admin.view.AbstractAttrPanel#onChangeRenderer(java.lang
     * .String, java.lang.String)
     */
    @Override
    protected void onChangeRenderer(String oldRenderer, String newRenderer) {
    }

}