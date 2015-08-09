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
 *  File Name: VirtualFabricManagement.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.1  2015/03/25 19:10:03  jijunwan
 *  Archive Log:    first version of VirtualFabric support
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.api.management.virtualfabrics.impl;

import static com.intel.stl.api.management.XMLConstants.NAME;
import static com.intel.stl.api.management.XMLConstants.VIRTUAL_FABRIC;
import static com.intel.stl.api.management.XMLConstants.VIRTUAL_FABRICS;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.intel.stl.api.IMessage;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.management.DuplicateNameException;
import com.intel.stl.api.management.FMConfHelper;
import com.intel.stl.api.management.XMLUtils;
import com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement;
import com.intel.stl.api.management.virtualfabrics.VirtualFabric;
import com.intel.stl.api.management.virtualfabrics.VirtualFabricException;
import com.intel.stl.api.management.virtualfabrics.VirtualFabrics;
import com.intel.stl.common.STLMessages;

public class VirtualFabricManagement implements IVirtualFabricManagement {
    private final static Logger log = LoggerFactory
            .getLogger(VirtualFabricManagement.class);

    private final static Set<String> RESERVED = new HashSet<String>() {
        private static final long serialVersionUID = -8507198541424973196L;

        {
            add("Default");
            add("Admin");
        }
    };

    private final FMConfHelper confHelp;

    /**
     * Description:
     * 
     * @param confHelp
     */
    public VirtualFabricManagement(FMConfHelper confHelp) {
        super();
        this.confHelp = confHelp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * getReservedVirtualFabrics()
     */
    @Override
    public Set<String> getReservedVirtualFabrics() {
        return RESERVED;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * getVirtualFabrics()
     */
    @Override
    public List<VirtualFabric> getVirtualFabrics()
            throws VirtualFabricException {
        try {
            File confFile = confHelp.getConfFile(false);
            VirtualFabrics vfs = unmarshal(confFile);
            log.info("Fetch " + vfs.getVFs().size()
                    + " Device Groups from host '" + confHelp.getHost() + "'");
            return vfs.getVFs();
        } catch (Exception e) {
            throw createVirtualFabricException(
                    STLMessages.STL63021_GET_VFS_ERR, e, confHelp.getHost(),
                    StringUtils.getErrorMessage(e));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * getVirtualFabric(java.lang.String)
     */
    @Override
    public VirtualFabric getVirtualFabric(String name)
            throws VirtualFabricException {
        try {
            File confFile = confHelp.getConfFile(false);
            VirtualFabrics vfs = unmarshal(confFile);
            return vfs.getVF(name);
        } catch (Exception e) {
            throw createVirtualFabricException(STLMessages.STL63026_GET_VF_ERR,
                    e, name, confHelp.getHost(), StringUtils.getErrorMessage(e));
        }
    }

    /**
     * <i>Description:</i>
     * 
     * @param confFile
     * @return
     */
    private VirtualFabrics unmarshal(File xmlFile) throws Exception {
        XMLInputFactory xif = XMLInputFactory.newFactory();
        StreamSource xml = new StreamSource(xmlFile);
        final XMLStreamReader xsr = xif.createXMLStreamReader(xml);
        while (xsr.hasNext()) {
            if (xsr.isStartElement()
                    && xsr.getLocalName().equals(VIRTUAL_FABRICS)) {
                break;
            }
            xsr.next();
        }

        JAXBContext jc = JAXBContext.newInstance(VirtualFabrics.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement<VirtualFabrics> jb =
                unmarshaller.unmarshal(xsr, VirtualFabrics.class);
        xsr.close();

        return jb.getValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * addVirtualFabric
     * (com.intel.stl.api.management.virtualfabrics.VirtualFabric)
     */
    @Override
    public void addVirtualFabric(VirtualFabric vf)
            throws VirtualFabricException {
        try {
            File confFile = confHelp.getConfFile(false);
            uniqueNameCheck(null, vf.getName());
            addVirtualFabric(confFile, confFile, vf);
            log.info("Added Virtual Fabric " + vf);
        } catch (Exception e) {
            throw createVirtualFabricException(STLMessages.STL63022_ADD_VF_ERR,
                    e, vf.getName(), confHelp.getHost(),
                    StringUtils.getErrorMessage(e));
        }
    }

    protected void addVirtualFabric(File srcXml, File dstXml, VirtualFabric vf)
            throws Exception {
        // transfer app to DOM
        DOMResult res = new DOMResult();
        JAXBContext context = JAXBContext.newInstance(vf.getClass());
        context.createMarshaller().marshal(vf, res);
        Document groupsDoc = (Document) res.getNode();
        Node newGroup = groupsDoc.getFirstChild();

        // read in old xml
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(srcXml);

        // check app in old xml
        Node groupsNode = doc.getElementsByTagName(VIRTUAL_FABRICS).item(0);
        Node matchedGroup = getVFByName(groupsNode, vf.getName());
        if (matchedGroup != null) {
            throw new IllegalArgumentException("Virtual Fabric '"
                    + vf.getName() + "' alreday exist!");
        }

        // append app to Applications node
        XMLUtils.appendNode(doc, groupsNode, newGroup);

        // save back to xml file
        XMLUtils.writeDoc(doc, dstXml);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * removeVirtualFabric(java.lang.String)
     */
    @Override
    public void removeVirtualFabric(String name) throws VirtualFabricException {
        try {
            File confFile = confHelp.getConfFile(false);
            removeVirtualFabric(confFile, confFile, name);
            log.info("Removed application '" + name + "'");
        } catch (Exception e) {
            throw createVirtualFabricException(
                    STLMessages.STL63023_REMOVE_VF_ERR, e, name,
                    confHelp.getHost(), StringUtils.getErrorMessage(e));
        }
    }

    protected void removeVirtualFabric(File srcXml, File dstXml, String name)
            throws Exception {
        // read in old xml
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(srcXml);

        // check app in old xml
        Node vfsNode = doc.getElementsByTagName(VIRTUAL_FABRICS).item(0);
        Node matchedVf = getVFByName(vfsNode, name);
        if (matchedVf != null) {
            XMLUtils.removeNode(doc, vfsNode, matchedVf, name);

            // save back to xml file
            XMLUtils.writeDoc(doc, dstXml);
        } else {
            throw new IllegalArgumentException("Couldn't find Virtual Fabric '"
                    + name + "'");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * updateVirtualFabric(java.lang.String,
     * com.intel.stl.api.management.virtualfabrics.VirtualFabric)
     */
    @Override
    public void updateVirtualFabric(String oldName, VirtualFabric vf)
            throws VirtualFabricException {
        try {
            File confFile = confHelp.getConfFile(false);
            if (!oldName.equals(vf.getName())) {
                VirtualFabrics groups = unmarshal(confFile);
                uniqueNameCheck(groups, vf.getName());
            }
            updateVirtualFabric(confFile, confFile, oldName, vf, false);
            log.info("Updated Virtual Fabric " + vf);
        } catch (Exception e) {
            throw createVirtualFabricException(
                    STLMessages.STL63024_UPDATE_VF_ERR, e, vf.getName(),
                    confHelp.getHost(), StringUtils.getErrorMessage(e));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * addOrUpdateVirtualFabric(java.lang.String,
     * com.intel.stl.api.management.virtualfabrics.VirtualFabric)
     */
    @Override
    public void addOrUpdateVirtualFabric(String oldName, VirtualFabric vf)
            throws VirtualFabricException {
        try {
            File confFile = confHelp.getConfFile(false);
            updateVirtualFabric(confFile, confFile, oldName, vf, true);
            log.info("Added or updated Virtual Fabric " + vf);
        } catch (Exception e) {
            throw createVirtualFabricException(
                    STLMessages.STL63025_ADDUPDATE_VF_ERR, e, vf.getName(),
                    confHelp.getHost(), StringUtils.getErrorMessage(e));
        }
    }

    protected void updateVirtualFabric(File srcXml, File dstXml,
            String oldName, VirtualFabric vf, boolean allowAdd)
            throws Exception {
        // transfer app to DOM
        DOMResult res = new DOMResult();
        JAXBContext context = JAXBContext.newInstance(vf.getClass());
        context.createMarshaller().marshal(vf, res);
        Document vfsDoc = (Document) res.getNode();
        Node newVf = vfsDoc.getFirstChild();

        // read in old xml
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(srcXml);

        doc.adoptNode(newVf);
        // check vf in old xml
        Node vfsNode = doc.getElementsByTagName(VIRTUAL_FABRICS).item(0);
        Node matchedVf = getVFByName(vfsNode, oldName);
        if (matchedVf == null) {
            if (allowAdd) {
                XMLUtils.appendNode(doc, vfsNode, newVf);
            } else {
                throw new IllegalArgumentException(
                        "Couldn't find Virtual Fabric '" + oldName + "'");
            }
        } else {
            XMLUtils.replaceNode(doc, vfsNode, matchedVf, newVf);
        }
        XMLUtils.writeDoc(doc, dstXml);
    }

    protected void uniqueNameCheck(VirtualFabrics vfs, String name)
            throws Exception {
        if (vfs == null) {
            File confFile = confHelp.getConfFile(false);
            vfs = unmarshal(confFile);
        }
        for (VirtualFabric group : vfs.getVFs()) {
            if (group.getName().equals(name)) {
                throw new DuplicateNameException(name);
            }
        }
    }

    private Node getVFByName(Node appsNode, String name) {
        NodeList children = appsNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals(VIRTUAL_FABRIC)) {
                Node nameNode = XMLUtils.getNodeByName(child, NAME);
                if (nameNode != null) {
                    if (nameNode.getTextContent().equals(name)) {
                        return child;
                    }
                }
            }
        }
        return null;
    }

    protected VirtualFabricException createVirtualFabricException(IMessage msg,
            Throwable error, Object... args) {
        return new VirtualFabricException(msg, error, args);
    }
}