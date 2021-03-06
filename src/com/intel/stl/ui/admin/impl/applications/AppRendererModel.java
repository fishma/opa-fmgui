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
 *  File Name: AppRenderModel.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.3  2015/08/17 18:54:53  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - changed frontend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2015/03/30 14:45:46  jijunwan
 *  Archive Log:    added subclasses for ListRenderer to avoid the compiler problem on Hudson server
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/03/30 14:25:41  jijunwan
 *  Archive Log:    1) introduced IRendererModel to create renderer only we nee
 *  Archive Log:    2) removed #getName from IAttrRenderer to provide more flexibilities and let IRendererModel to take care which attribute should use which renderer, how to init it properly
 *  Archive Log:    3) improved to support repeatable and non-repeatable attributes. For non-repeatable attributes, we only can add once
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.ui.admin.impl.applications;

import java.util.LinkedHashMap;
import java.util.Map;

import com.intel.stl.api.management.XMLConstants;
import com.intel.stl.api.management.applications.IncludeApplication;
import com.intel.stl.ui.admin.impl.IRendererModel;
import com.intel.stl.ui.admin.view.IAttrRenderer;
import com.intel.stl.ui.admin.view.applications.AppSelectRenderer;
import com.intel.stl.ui.admin.view.applications.IncludeApplicationRenderer;
import com.intel.stl.ui.admin.view.applications.MGIDMaskedRenderer;
import com.intel.stl.ui.admin.view.applications.MGIDRangeRenderer;
import com.intel.stl.ui.admin.view.applications.MGIDRenderer;
import com.intel.stl.ui.admin.view.applications.ServiceIDMaskedRenderer;
import com.intel.stl.ui.admin.view.applications.ServiceIDRangeRenderer;
import com.intel.stl.ui.admin.view.applications.ServiceIDRenderer;

/**
 * we are using class.newInstance() to create a Renderer. All renderer we use
 * here must support empty construct!
 */
public class AppRendererModel implements IRendererModel {
    private final static Map<String, Class<? extends IAttrRenderer<?>>> map =
            new LinkedHashMap<String, Class<? extends IAttrRenderer<?>>>() {
                private static final long serialVersionUID =
                        -5530685307771587530L;

                {
                    put(XMLConstants.SERVICEID, ServiceIDRenderer.class);
                    put(XMLConstants.SERVICEID_RANGE,
                            ServiceIDRangeRenderer.class);
                    put(XMLConstants.SERVICEID_MASKED,
                            ServiceIDMaskedRenderer.class);
                    put(XMLConstants.MGID, MGIDRenderer.class);
                    put(XMLConstants.MGID_RANGE, MGIDRangeRenderer.class);
                    put(XMLConstants.MGID_MASKED, MGIDMaskedRenderer.class);
                    put(XMLConstants.SELECT, AppSelectRenderer.class);
                    put(XMLConstants.INCLUDE_APPLICATION,
                            IncludeApplicationRenderer.class);
                }
            };

    private String[] appNames;

    /**
     * Description:
     * 
     * @param appNames
     */
    public AppRendererModel() {
        super();
    }

    /**
     * @param appNames
     *            the appNames to set
     */
    public void setAppNames(String[] appNames) {
        this.appNames = appNames;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.impl.IRendererModel#getRendererNames()
     */
    @Override
    public String[] getRendererNames() {
        return map.keySet().toArray(new String[0]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.admin.impl.IRendererModel#getRenderer(java.lang.String)
     */
    @Override
    public IAttrRenderer<?> getRenderer(String name) throws Exception {
        Class<? extends IAttrRenderer<?>> klass = map.get(name);
        if (klass != null) {
            IAttrRenderer<?> res = klass.newInstance();
            initRenderer(name, res);
            return res;
        } else {
            throw new IllegalArgumentException("Unknown renderer '" + name
                    + "'");
        }
    }

    protected void initRenderer(String name, IAttrRenderer<?> renderer) {
        if (name.equals(XMLConstants.INCLUDE_APPLICATION)) {
            IncludeApplicationRenderer lsRenderer =
                    (IncludeApplicationRenderer) renderer;
            lsRenderer.setList(IncludeApplication.toArry(appNames));
        }
    }
}
