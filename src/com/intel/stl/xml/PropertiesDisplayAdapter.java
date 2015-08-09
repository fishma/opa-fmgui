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
 *  File Name: DevicePropertiesAdapter.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.3  2014/10/21 13:38:10  fernande
 *  Archive Log:    Adding displayed= attribute to the group tag in UserOptions XML
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/10/13 21:01:48  fernande
 *  Archive Log:    Added support for valueHeader attribute in UserOptions XML.
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/09/29 18:51:55  fernande
 *  Archive Log:    Adding UserOptions XML and  saving it to the database. Includes XML schema validation.
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: fernande
 *
 ******************************************************************************/

package com.intel.stl.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.MarshalException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.intel.stl.api.configuration.PropertyCategory;
import com.intel.stl.api.configuration.PropertyGroup;
import com.intel.stl.api.configuration.ResourceCategory;
import com.intel.stl.api.configuration.ResourceType;

/**
 * Converts the XML elements underneath PropertiesDisplay during
 * marshaling/unmarshaling to/from the XML types that represent the options for
 * the Properties Display pages (Performance tab in the UI) into a map that can
 * be easily processed in the UI. Also, this needs to be done because we cannot
 * expose the annotated JAXB types to the API (it would create a dependency for
 * the UI).
 */
public class PropertiesDisplayAdapter extends
        XmlAdapter<PropertiesDisplay, Map<ResourceType, List<PropertyGroup>>> {

    @Override
    public Map<ResourceType, List<PropertyGroup>> unmarshal(
            PropertiesDisplay displayOptions) throws Exception {
        Map<ResourceType, List<PropertyGroup>> options =
                new HashMap<ResourceType, List<PropertyGroup>>();
        List<ResourceClassType> resources =
                displayOptions.getResourceClassTypes();
        for (ResourceClassType resource : resources) {
            ResourceType resourceType = resource.getResourceType();
            List<GroupType> groups = resource.getGroups();
            List<PropertyGroup> propertyGroups =
                    new ArrayList<PropertyGroup>(groups.size());
            for (GroupType group : groups) {
                PropertyGroup propertyGroup = new PropertyGroup();
                propertyGroup.setName(group.getName());
                propertyGroup.setTitle(group.getTitle());
                propertyGroup.setDisplayed(group.getDisplayed());
                List<ResourceCategoryType> categories = group.getCategory();
                List<PropertyCategory> resourceCategories =
                        new ArrayList<PropertyCategory>(categories.size());
                for (ResourceCategoryType category : categories) {
                    PropertyCategory resourceCategory = new PropertyCategory();
                    resourceCategory.setResourceCategory(category
                            .getResourceCategory());
                    resourceCategory.setKeyHeader(category.getValue());
                    resourceCategory.setShowHeader(category.isShowHeader());
                    resourceCategory.setValueHeader(category.getValueHeader());

                    resourceCategories.add(resourceCategory);
                }
                propertyGroup.setCategories(resourceCategories);

                propertyGroups.add(propertyGroup);
            }
            options.put(resourceType, propertyGroups);
        }
        return options;
    }

    @Override
    public PropertiesDisplay marshal(
            Map<ResourceType, List<PropertyGroup>> options) throws Exception {
        PropertiesDisplay displayOptions = new PropertiesDisplay();
        List<ResourceClassType> classTypes =
                displayOptions.getResourceClassTypes();
        Iterator<ResourceType> it = options.keySet().iterator();
        while (it.hasNext()) {
            ResourceType resourceType = it.next();
            List<PropertyGroup> groups = options.get(resourceType);
            ResourceClassType resource;
            switch (resourceType) {
                case HFI:
                    resource = new HfiType();
                    resource.setGroups(createGroups(groups,
                            new HfiTypeFactory()));
                    break;
                case PORT:
                    resource = new PortType();
                    resource.setGroups(createGroups(groups,
                            new PortTypeFactory()));
                    break;
                case SWITCH:
                    resource = new SwitchType();
                    resource.setGroups(createGroups(groups,
                            new SwitchTypeFactory()));
                    break;
                default:
                    resource = null;
            }
            classTypes.add(resource);

        }
        return displayOptions;
    }

    private List<GroupType> createGroups(List<PropertyGroup> propertyGroups,
            XmlTypeFactory factory) throws Exception {
        List<GroupType> groups =
                new ArrayList<GroupType>(propertyGroups.size());
        for (PropertyGroup propertyGroup : propertyGroups) {
            GroupType group = factory.createGroupTypeFrom(propertyGroup);
            List<PropertyCategory> propertyCategories =
                    propertyGroup.getCategories();
            if (propertyCategories != null) {
                List<ResourceCategoryType> categories = group.getCategory();

                for (PropertyCategory propertyCategory : propertyCategories) {
                    ResourceCategoryType category =
                            factory.createResourceCategoryTypeFrom(propertyCategory);
                    categories.add(category);
                }
            }
            groups.add(group);
        }
        return groups;
    }

    private abstract class XmlTypeFactory {
        protected void populateGroupType(GroupType groupType,
                PropertyGroup group) {
            groupType.setName(group.getName());
            groupType.setTitle(group.getTitle());
            groupType.setDisplayed(group.isDisplayed());
        }

        protected void populateResourceCategoryType(
                ResourceCategoryType categoryType, PropertyCategory category) {
            categoryType.setShowHeader(category.isShowHeader());
            categoryType.setValue(category.getKeyHeader());
            categoryType.setValueHeader(category.getValueHeader());
        }

        protected abstract GroupType createGroupTypeFrom(PropertyGroup group);

        protected abstract ResourceCategoryType createResourceCategoryTypeFrom(
                PropertyCategory category) throws Exception;
    }

    private class HfiTypeFactory extends XmlTypeFactory {

        @Override
        public GroupType createGroupTypeFrom(PropertyGroup group) {
            HfiGroupType hfiGroup = new HfiGroupType();
            populateGroupType(hfiGroup, group);
            return hfiGroup;
        }

        @Override
        public ResourceCategoryType createResourceCategoryTypeFrom(
                PropertyCategory category) throws Exception {
            HfiCategoryType hfiCategory = new HfiCategoryType();
            populateResourceCategoryType(hfiCategory, category);
            ResourceCategory rc = category.getResourceCategory();
            if (rc != null) {
                HfiCategory value = HfiCategory.fromValue(rc.name());
                hfiCategory.setName(value);
            }
            return hfiCategory;
        }
    }

    private class PortTypeFactory extends XmlTypeFactory {

        @Override
        public GroupType createGroupTypeFrom(PropertyGroup group) {
            PortGroupType portGroup = new PortGroupType();
            populateGroupType(portGroup, group);
            return portGroup;
        }

        @Override
        public ResourceCategoryType createResourceCategoryTypeFrom(
                PropertyCategory category) throws Exception {
            PortCategoryType portCategory = new PortCategoryType();
            populateResourceCategoryType(portCategory, category);
            ResourceCategory rc = category.getResourceCategory();
            if (rc != null) {
                PortCategory value = PortCategory.fromValue(rc.name());
                portCategory.setName(value);
            }
            return portCategory;
        }
    }

    private class SwitchTypeFactory extends XmlTypeFactory {

        @Override
        public GroupType createGroupTypeFrom(PropertyGroup group) {
            SwitchGroupType switchGroup = new SwitchGroupType();
            populateGroupType(switchGroup, group);
            return switchGroup;
        }

        @Override
        public ResourceCategoryType createResourceCategoryTypeFrom(
                PropertyCategory category) throws MarshalException {
            SwitchCategoryType switchCategory = new SwitchCategoryType();
            populateResourceCategoryType(switchCategory, category);
            ResourceCategory rc = category.getResourceCategory();
            if (rc != null) {
                SwitchCategory value = SwitchCategory.fromValue(rc.name());
                switchCategory.setName(value);
            }
            return switchCategory;
        }
    }
}