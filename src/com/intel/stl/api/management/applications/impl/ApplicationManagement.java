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
 *  File Name: ApplicationsMarshaller.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.1  2015/03/16 22:01:00  jijunwan
 *  Archive Log:    changed package name from application to applications, and from devicegroup to devicegroups
 *  Archive Log:    Added #getType to ServiceID, MGID, LongNode and their subclasses,
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2015/03/13 20:57:03  jijunwan
 *  Archive Log:    minor  improvement on FM Application
 *  Archive Log:    Added support on FM DeviceGroup
 *  Archive Log:    put all constants used in xml file to XMLConstants
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2015/03/13 14:29:16  jijunwan
 *  Archive Log:    improved to have consistent xml format when we add, remove or replace an Application, Device Group, or Virtual Fabric node.
 *  Archive Log:    improved to add comments to indicate the change was made by FM GUI and also the time we change it
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/03/05 17:30:41  jijunwan
 *  Archive Log:    init version to support Application management
 *  Archive Log:    1) read/write opafm.xml from/to host with backup file support
 *  Archive Log:    2) Application parser
 *  Archive Log:    3) Add/remove and update Application
 *  Archive Log:    4) unique name, reference conflication check
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.api.management.applications.impl;

import static com.intel.stl.api.management.XMLConstants.APPLICATION;
import static com.intel.stl.api.management.XMLConstants.APPLICATIONS;
import static com.intel.stl.api.management.XMLConstants.NAME;

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
import com.intel.stl.api.management.ReferenceConflictException;
import com.intel.stl.api.management.XMLUtils;
import com.intel.stl.api.management.applications.Application;
import com.intel.stl.api.management.applications.ApplicationException;
import com.intel.stl.api.management.applications.Applications;
import com.intel.stl.api.management.applications.IApplicationManangement;
import com.intel.stl.common.STLMessages;

public class ApplicationManagement implements IApplicationManangement {
    private final static Logger log = LoggerFactory
            .getLogger(ApplicationManagement.class);

    private final static Set<String> RESERVED = new HashSet<String>() {
        private static final long serialVersionUID = -8507198541424973196L;

        {
            add("All");
            add("SA");
            add("PA");
            add("PM");
            add("AllOthers");
        }
    };

    private final FMConfHelper confHelp;

    /**
     * Description:
     * 
     * @param confHelp
     */
    public ApplicationManagement(FMConfHelper confHelp) {
        super();
        this.confHelp = confHelp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.management.applications.IApplicationManangement#
     * getReservedApplications()
     */
    @Override
    public Set<String> getReservedApplications() {
        return RESERVED;
    }

    @Override
    public synchronized List<Application> getApplications()
            throws ApplicationException {
        try {
            File confFile = confHelp.getConfFile(false);
            Applications apps = unmarshal(confFile);
            log.info("Fetch " + apps.getApplications().size()
                    + " applications from host '" + confHelp.getHost() + "'");
            return apps.getApplications();
        } catch (Exception e) {
            throw createApplicationException(STLMessages.STL63001_GET_APPS_ERR,
                    e, confHelp.getHost(), StringUtils.getErrorMessage(e));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.management.application.IApplicationManangement#
     * getApplication(java.lang.String)
     */
    @Override
    public synchronized Application getApplication(String name)
            throws ApplicationException {
        try {
            File confFile = confHelp.getConfFile(false);
            Applications apps = unmarshal(confFile);
            return apps.getApplication(name);
        } catch (Exception e) {
            throw createApplicationException(STLMessages.STL63006_GET_APP_ERR,
                    e, name, confHelp.getHost(), StringUtils.getErrorMessage(e));
        }
    }

    protected Applications unmarshal(File xmlFile) throws Exception {
        XMLInputFactory xif = XMLInputFactory.newFactory();
        StreamSource xml = new StreamSource(xmlFile);
        final XMLStreamReader xsr = xif.createXMLStreamReader(xml);
        while (xsr.hasNext()) {
            if (xsr.isStartElement() && xsr.getLocalName().equals(APPLICATIONS)) {
                break;
            }
            xsr.next();
        }

        JAXBContext jc = JAXBContext.newInstance(Applications.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement<Applications> jb =
                unmarshaller.unmarshal(xsr, Applications.class);
        xsr.close();

        return jb.getValue();
    }

    @Override
    public synchronized void addApplication(Application app)
            throws ApplicationException {
        try {
            File confFile = confHelp.getConfFile(false);
            uniqueNameCheck(null, app.getName());
            // TODO loop check
            addApplication(confFile, confFile, app);
            log.info("Added application " + app);
        } catch (Exception e) {
            throw createApplicationException(STLMessages.STL63002_ADD_APP_ERR,
                    e, app.getName(), confHelp.getHost(),
                    StringUtils.getErrorMessage(e));
        }
    }

    protected void addApplication(File srcXml, File dstXml, Application app)
            throws Exception {
        // transfer app to DOM
        DOMResult res = new DOMResult();
        JAXBContext context = JAXBContext.newInstance(app.getClass());
        context.createMarshaller().marshal(app, res);
        Document appsDoc = (Document) res.getNode();
        Node newApp = appsDoc.getFirstChild();

        // read in old xml
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(srcXml);

        // check app in old xml
        Node appsNode = doc.getElementsByTagName(APPLICATIONS).item(0);
        Node matchedApp = getApplicationByName(appsNode, app.getName());
        if (matchedApp != null) {
            throw new IllegalArgumentException("Application '" + app.getName()
                    + "' alreday exist!");
        }

        // append app to Applications node
        XMLUtils.appendNode(doc, appsNode, newApp);

        // save back to xml file
        XMLUtils.writeDoc(doc, dstXml);
    }

    @Override
    public synchronized void removeApplication(String name)
            throws ApplicationException {
        try {
            File confFile = confHelp.getConfFile(false);
            referenceCheck(null, name);
            removeApplication(confFile, confFile, name);
            log.info("Removed application '" + name + "'");
        } catch (Exception e) {
            throw createApplicationException(
                    STLMessages.STL63003_REMOVE_APP_ERR, e, name,
                    confHelp.getHost(), StringUtils.getErrorMessage(e));
        }
    }

    protected void removeApplication(File srcXml, File dstXml, String name)
            throws Exception {
        // read in old xml
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(srcXml);

        // check app in old xml
        Node appsNode = doc.getElementsByTagName(APPLICATIONS).item(0);
        Node matchedApp = getApplicationByName(appsNode, name);
        if (matchedApp != null) {
            XMLUtils.removeNode(doc, appsNode, matchedApp, name);

            // save back to xml file
            XMLUtils.writeDoc(doc, dstXml);
        } else {
            throw new IllegalArgumentException("Couldn't find Application '"
                    + name + "'");
        }
    }

    @Override
    public synchronized void updateApplication(String oldName, Application app)
            throws ApplicationException {
        try {
            File confFile = confHelp.getConfFile(false);
            if (!oldName.equals(app.getName())) {
                Applications apps = unmarshal(confFile);
                uniqueNameCheck(apps, app.getName());
                referenceCheck(apps, oldName);
            }
            // TODO loop check
            updateApplication(confFile, confFile, oldName, app, false);
            log.info("Updated application " + app);
        } catch (Exception e) {
            throw createApplicationException(
                    STLMessages.STL63004_UPDATE_APP_ERR, e, app.getName(),
                    confHelp.getHost(), StringUtils.getErrorMessage(e));
        }
    }

    @Override
    public synchronized void addOrUpdateApplication(String oldName,
            Application app) throws ApplicationException {
        try {
            File confFile = confHelp.getConfFile(false);
            referenceCheck(null, oldName);
            // TODO loop check
            updateApplication(confFile, confFile, oldName, app, true);
            log.info("Added or updated application " + app);
        } catch (Exception e) {
            throw createApplicationException(
                    STLMessages.STL63005_ADDUPDATE_APP_ERR, e, app.getName(),
                    confHelp.getHost(), StringUtils.getErrorMessage(e));
        }
    }

    protected void updateApplication(File srcXml, File dstXml, String oldName,
            Application app, boolean allowAdd) throws Exception {
        // transfer app to DOM
        DOMResult res = new DOMResult();
        JAXBContext context = JAXBContext.newInstance(app.getClass());
        context.createMarshaller().marshal(app, res);
        Document appsDoc = (Document) res.getNode();
        Node newApp = appsDoc.getFirstChild();

        // read in old xml
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(srcXml);

        doc.adoptNode(newApp);
        // check app in old xml
        Node appsNode = doc.getElementsByTagName(APPLICATIONS).item(0);
        Node matchedApp = getApplicationByName(appsNode, oldName);
        if (matchedApp == null) {
            if (allowAdd) {
                XMLUtils.appendNode(doc, appsNode, newApp);
            } else {
                throw new IllegalArgumentException(
                        "Couldn't find Application '" + oldName + "'");
            }
        } else {
            XMLUtils.replaceNode(doc, appsNode, matchedApp, newApp);
        }
        XMLUtils.writeDoc(doc, dstXml);
    }

    protected void referenceCheck(Applications apps, String name)
            throws Exception {
        if (apps == null) {
            File confFile = confHelp.getConfFile(false);
            apps = unmarshal(confFile);
        }
        List<Application> refs = apps.getReferencedApplications(name);
        if (!refs.isEmpty()) {
            String[] refNames = new String[refs.size()];
            for (int i = 0; i < refNames.length; i++) {
                refNames[i] = refs.get(i).getName();
            }
            throw new ReferenceConflictException(name, refNames);
        }
    }

    protected void uniqueNameCheck(Applications apps, String name)
            throws Exception {
        if (apps == null) {
            File confFile = confHelp.getConfFile(false);
            apps = unmarshal(confFile);
        }
        for (Application app : apps.getApplications()) {
            if (app.getName().equals(name)) {
                throw new DuplicateNameException(name);
            }
        }
    }

    private Node getApplicationByName(Node appsNode, String name) {
        NodeList children = appsNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals(APPLICATION)) {
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

    protected ApplicationException createApplicationException(IMessage msg,
            Throwable error, Object... args) {
        return new ApplicationException(msg, error, args);
    }

}