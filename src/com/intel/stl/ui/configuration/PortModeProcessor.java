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
 *  File Name: PortModeProcessor.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.4  2015/08/17 18:53:50  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - changed frontend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2015/01/27 19:44:31  jijunwan
 *  Archive Log:    updated PortInfo to match FM 314 (stl_sm.h v1.125)
 *  Archive Log:      - removed IsSCtoSCMappingEnabled
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/10/22 01:47:47  jijunwan
 *  Archive Log:    renamed
 *  Archive Log:    PropertyPageCategory to DevicePropertyCategory,
 *  Archive Log:    PropertyItem to DevicePropertyItem,
 *  Archive Log:    PropertyPageGroup to DevicePropertyGroup
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/10/13 21:05:30  fernande
 *  Archive Log:    Changed GetDevicePropertiesTask to be driven by the PropertiesDisplayOptions in UserSettings instead of hard coded
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: fernande
 *
 ******************************************************************************/

package com.intel.stl.ui.configuration;

import static com.intel.stl.ui.common.STLConstants.K0385_TRUE;
import static com.intel.stl.ui.common.STLConstants.K0386_FALSE;
import static com.intel.stl.ui.model.DeviceProperty.ACTIVE_OPTIMIZE;
import static com.intel.stl.ui.model.DeviceProperty.PASSTHRU;
import static com.intel.stl.ui.model.DeviceProperty.TRAP_QUERY_16B;
import static com.intel.stl.ui.model.DeviceProperty.VL_MARKER;

import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.ui.model.DevicePropertyCategory;

public class PortModeProcessor extends BaseCategoryProcessor {

    @Override
    public void process(ICategoryProcessorContext context,
            DevicePropertyCategory category) {
        PortInfoBean portInfo = context.getPortInfo();

        if (portInfo == null) {
            getEmptyPortMode(category);
            return;
        }
        String trueStr = K0385_TRUE.getValue();
        String falseStr = K0386_FALSE.getValue();
        String value = falseStr;
        if (portInfo.isActiveOptimizeEnabled()) {
            value = trueStr;
        }
        addProperty(category, ACTIVE_OPTIMIZE, value);
        value = falseStr;
        if (portInfo.isPassThroughEnabled()) {
            value = trueStr;
        }
        addProperty(category, PASSTHRU, value);
        value = falseStr;
        if (portInfo.isVLMarkerEnabled()) {
            value = trueStr;
        }
        addProperty(category, VL_MARKER, value);
        value = falseStr;
        if (portInfo.is16BTrapQueryEnabled()) {
            value = trueStr;
        }
        addProperty(category, TRAP_QUERY_16B, value);
    }

    private void getEmptyPortMode(DevicePropertyCategory category) {
        addProperty(category, ACTIVE_OPTIMIZE, "");
        addProperty(category, PASSTHRU, "");
        addProperty(category, VL_MARKER, "");
        addProperty(category, TRAP_QUERY_16B, "");
    }
}
