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
 *  File Name: VirtualFabricEditorPanel.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.8  2015/08/17 18:54:01  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - changed frontend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.7  2015/08/04 18:06:13  jijunwan
 *  Archive Log:    PR 129812 - Continuous "abandon changes" message when a virtual fabric is removed
 *  Archive Log:    - removed code that reset App names and DG names.
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2015/07/14 17:06:03  jijunwan
 *  Archive Log:    PR 129541 - Should forbid save or deploy when there is invalid edit on management panel
 *  Archive Log:    - throw InvalidEditException when there is invalid edit
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2015/05/14 17:19:46  jijunwan
 *  Archive Log:    PR 128697 - Handle empty list of items
 *  Archive Log:    - Added code to handle null item
 *  Archive Log:    - Added code to clean panel when it gets a null item
 *  Archive Log:    - Enable/disable buttons properly when we get an empty item list or null item
 *  Archive Log:    - Improved to handle item selection when the index is invalid, such as -1
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2015/04/06 11:14:12  jypak
 *  Archive Log:    Klockwork: Front End Critical Without Unit Test. Open issues fixed.
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2015/04/03 14:28:20  jijunwan
 *  Archive Log:    added title border
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2015/03/30 14:25:37  jijunwan
 *  Archive Log:    1) introduced IRendererModel to create renderer only we nee
 *  Archive Log:    2) removed #getName from IAttrRenderer to provide more flexibilities and let IRendererModel to take care which attribute should use which renderer, how to init it properly
 *  Archive Log:    3) improved to support repeatable and non-repeatable attributes. For non-repeatable attributes, we only can add once
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/03/27 15:47:46  jijunwan
 *  Archive Log:    first version of VirtualFabric UI
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.ui.admin.view.virtualfabrics;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.VerticalLayout;

import com.intel.stl.api.management.IAttribute;
import com.intel.stl.api.management.virtualfabrics.Enable;
import com.intel.stl.api.management.virtualfabrics.Qos;
import com.intel.stl.api.management.virtualfabrics.Security;
import com.intel.stl.api.management.virtualfabrics.VirtualFabric;
import com.intel.stl.ui.admin.impl.virtualfabrics.VirtualFabricRendererModel;
import com.intel.stl.ui.admin.view.AbstractEditorPanel;
import com.intel.stl.ui.admin.view.IAttrRenderer;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.ComponentFactory;

public class VirtualFabricsEditorPanel extends
        AbstractEditorPanel<VirtualFabric> {
    private static final long serialVersionUID = -6284544057548609879L;

    private JPanel mainPanel;

    private JPanel basicPanel;

    private JCheckBox enableBox;

    private JCheckBox securityBox;

    private JCheckBox qosBox;

    private JPanel attrsPanel;

    private final List<VirtualFabricAttrPanel> attrPanels =
            new ArrayList<VirtualFabricAttrPanel>();

    private VirtualFabricAttrPanel addAttrPanel;

    private final VirtualFabricRendererModel rendererModel;

    /**
     * Description:
     * 
     * @param rendererModel
     */
    public VirtualFabricsEditorPanel(VirtualFabricRendererModel rendererModel) {
        super();
        this.rendererModel = rendererModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.view.AbstractEditorPanel#getMainComponent()
     */
    @Override
    protected JComponent getMainComponent() {
        if (mainPanel == null) {
            mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(UIConstants.INTEL_WHITE);
            mainPanel.setBorder(BorderFactory
                    .createTitledBorder(STLConstants.K2112_ATTRIBUTES
                            .getValue()));

            JPanel panel = getBasicPanel();
            mainPanel.add(panel, BorderLayout.NORTH);

            attrsPanel = new JPanel(new VerticalLayout(10));
            attrsPanel.setOpaque(false);
            JScrollPane scrollPane = new JScrollPane(attrsPanel);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
        }
        return mainPanel;
    }

    protected JPanel getBasicPanel() {
        if (basicPanel == null) {
            basicPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 5));
            basicPanel.setOpaque(false);

            enableBox =
                    ComponentFactory.getIntelCheckBox(STLConstants.K0445_ENABLE
                            .getValue());
            basicPanel.add(enableBox);

            securityBox =
                    ComponentFactory
                            .getIntelCheckBox(STLConstants.K0072_SECURITY
                                    .getValue());
            basicPanel.add(securityBox);

            qosBox =
                    ComponentFactory.getIntelCheckBox(STLConstants.K2141_QOS
                            .getValue());
            basicPanel.add(qosBox);
        }
        return basicPanel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.view.AbstractEditorPanel#clear()
     */
    @Override
    public void clear() {
        super.clear();
        attrsPanel.removeAll();
        attrPanels.clear();
        enableBox.setSelected(false);
        enableBox.setEnabled(false);
        securityBox.setSelected(false);
        securityBox.setEnabled(false);
        qosBox.setSelected(false);
        qosBox.setEnabled(false);
        revalidate();
        repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.admin.view.AbstractEditorPanel#showItemObject(java.lang
     * .Object, java.lang.String[], boolean)
     */
    @Override
    protected void showItemObject(VirtualFabric vf, String[] itemNames,
            boolean isEditable) {
        attrsPanel.removeAll();
        attrPanels.clear();

        setBasicAttrs(vf, isEditable);

        List<IAttribute> attrs = getAdvancedAttrs(vf);
        String[] attrNAmes = addAttrs(attrs, isEditable);

        if (isEditable) {
            addAttrPanel = new VirtualFabricAttrPanel(this, rendererModel);
            addAttrPanel.setDisabledAttrs(attrNAmes);
            attrsPanel.add(addAttrPanel);
        }

        revalidate();
        repaint();
    }

    protected void setBasicAttrs(VirtualFabric vf, boolean isEditable) {
        enableBox.setSelected(vf.getEnable().isSelected());
        enableBox.setEnabled(isEditable);
        securityBox.setSelected(vf.getSecurity().isSelected());
        securityBox.setEnabled(isEditable);
        qosBox.setSelected(vf.getQos().isSelected());
        qosBox.setEnabled(isEditable);
    }

    protected List<IAttribute> getAdvancedAttrs(VirtualFabric vf) {
        List<IAttribute> attrs = new ArrayList<IAttribute>();
        if (vf.getPKey() != null) {
            attrs.add(vf.getPKey());
        }
        if (vf.getMaxMtu() != null) {
            attrs.add(vf.getMaxMtu());
        }
        if (vf.getMaxRate() != null) {
            attrs.add(vf.getMaxRate());
        }
        if (vf.getStandby() != null) {
            attrs.add(vf.getStandby());
        }
        if (vf.getHighPriority() != null) {
            attrs.add(vf.getHighPriority());
        }
        if (vf.getBandwidth() != null) {
            attrs.add(vf.getBandwidth());
        }
        if (vf.getPktLifeTimeMult() != null) {
            attrs.add(vf.getPktLifeTimeMult());
        }
        if (vf.getBaseSL() != null) {
            attrs.add(vf.getBaseSL());
        }
        if (vf.getFlowControlDisable() != null) {
            attrs.add(vf.getFlowControlDisable());
        }
        if (vf.getPreemptRank() != null) {
            attrs.add(vf.getPreemptRank());
        }
        if (vf.getHoqLife() != null) {
            attrs.add(vf.getHoqLife());
        }
        if (vf.getMembers() != null) {
            attrs.addAll(vf.getMembers());
        }
        if (vf.getApplications() != null) {
            attrs.addAll(vf.getApplications());
        }
        return attrs;
    }

    protected String[] addAttrs(List<IAttribute> attrs, boolean isEditable) {
        Set<String> attrNameSet = new HashSet<String>();
        for (IAttribute attr : attrs) {
            VirtualFabricAttrPanel attrPanel =
                    new VirtualFabricAttrPanel(this, rendererModel);
            attrPanel.setAttr(attr.getType(), attr, isEditable);
            attrsPanel.add(attrPanel);
            attrPanels.add(attrPanel);
            String rendererName = attrPanel.getRendererName();
            if (!rendererModel.isRepeatabledAttr(rendererName)) {
                attrNameSet.add(rendererName);
            }
        }

        String[] attrNames = attrNameSet.toArray(new String[0]);
        for (VirtualFabricAttrPanel vfap : attrPanels) {
            vfap.setDisabledAttrs(attrNames);
        }
        return attrNames;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.admin.view.AbstractEditorPanel#updateItemObject(java
     * .lang.Object)
     */
    @Override
    protected void updateItemObject(VirtualFabric obj) {
        obj.setName(getCurrentName());
        obj.setEnable(new Enable(enableBox.isSelected()));
        obj.setSecurity(new Security(securityBox.isSelected()));
        obj.setQos(new Qos(qosBox.isSelected()));
        obj.clear();
        for (VirtualFabricAttrPanel attrPanel : attrPanels) {
            IAttrRenderer<? extends IAttribute> renderer =
                    attrPanel.getAttrRenderer();
            if (renderer != null) {
                IAttribute attr = renderer.getAttr();
                if (attr != null) {
                    attr.installVirtualFabric(obj);
                }
            }
        }
    }

    /**
     * <i>Description:</i>
     * 
     * @param vfAttrPanel
     */
    public void beginEdit(VirtualFabricAttrPanel attrPanel) {
        String name = attrPanel.getRendererName();
        boolean toDisable = !rendererModel.isRepeatabledAttr(name);
        Set<String> attrNameSet = new HashSet<String>();
        for (VirtualFabricAttrPanel vfap : attrPanels) {
            if (toDisable) {
                vfap.addDisabledAttr(name);
            }
            if (!rendererModel.isRepeatabledAttr(vfap.getRendererName())) {
                attrNameSet.add(vfap.getRendererName());
            }
        }

        attrPanels.add(attrPanel);
        if (toDisable) {
            attrNameSet.add(name);
        }
        attrPanel.setDisabledAttrs(attrNameSet.toArray(new String[0]));

        addAttrPanel = new VirtualFabricAttrPanel(this, rendererModel);
        addAttrPanel.setDisabledAttrs(attrNameSet.toArray(new String[0]));
        attrsPanel.add(addAttrPanel);
    }

    /**
     * <i>Description:</i>
     * 
     * @param vfAttrPanel
     */
    public void removeEditor(VirtualFabricAttrPanel attrPanel) {
        attrsPanel.remove(attrPanel);
        String name = attrPanel.getRendererName();
        if (!rendererModel.isRepeatabledAttr(name)) {
            for (VirtualFabricAttrPanel vfap : attrPanels) {
                vfap.removeDisabledAttr(name);
            }
        }
        addAttrPanel.removeDisabledAttr(name);
        attrPanels.remove(attrPanel);
        revalidate();
    }

    /**
     * <i>Description:</i>
     * 
     * @param oldRenderer
     * @param newRenderer
     */
    public void changeEditorRenderer(String oldRenderer, String newRenderer) {
        boolean disableOld = !rendererModel.isRepeatabledAttr(oldRenderer);
        boolean disableNew = !rendererModel.isRepeatabledAttr(newRenderer);
        for (VirtualFabricAttrPanel vfap : attrPanels) {
            if (disableNew) {
                vfap.addDisabledAttr(newRenderer);
            }
            if (disableOld) {
                vfap.removeDisabledAttr(oldRenderer);
            }
        }
        if (addAttrPanel != null) {
            if (disableNew) {
                addAttrPanel.addDisabledAttr(newRenderer);
            }
            if (disableOld) {
                addAttrPanel.removeDisabledAttr(oldRenderer);
            }
        }
    }

    /**
     * <i>Description:</i>
     * 
     * @param applications
     */
    public void setApplicationNames(List<String> applications) {
        rendererModel.setAppNames(applications.toArray(new String[0]));
    }

    /**
     * <i>Description:</i>
     * 
     * @param devicegroups
     */
    public void setDeviceGroupNames(List<String> devicegroups) {
        rendererModel.setDgNames(devicegroups.toArray(new String[0]));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.view.AbstractEditorPanel#isEditValid()
     */
    @Override
    protected boolean isEditValid() {
        if (!super.isEditValid()) {
            return false;
        }

        for (VirtualFabricAttrPanel attrPanel : attrPanels) {
            IAttrRenderer<?> renderer = attrPanel.getAttrRenderer();
            if (renderer != null && !renderer.isEditValid()) {
                return false;
            }
        }
        return true;
    }

}
