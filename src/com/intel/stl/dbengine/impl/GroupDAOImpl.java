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
 *  File Name: GroupDAOImpl.java
 *
 *
 *  Overview: 
 *
 *  @author: jypak
 *
 ******************************************************************************/

package com.intel.stl.dbengine.impl;

import static com.intel.stl.common.STLMessages.STL30045_GROUP_CONFIG_NOT_FOUND;
import static com.intel.stl.common.STLMessages.STL30053_PORT_CONFIG_NOT_FOUND_SUBNET;
import static com.intel.stl.common.STLMessages.STL30054_GROUP_INFO_NOT_FOUND_TIME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.DatabaseException;
import com.intel.stl.api.performance.GroupInfoBean;
import com.intel.stl.api.performance.GroupListBean;
import com.intel.stl.api.performance.PerformanceDataNotFoundException;
import com.intel.stl.api.performance.PortConfigBean;
import com.intel.stl.datamanager.GroupConfigId;
import com.intel.stl.datamanager.GroupConfigRecord;
import com.intel.stl.datamanager.GroupInfoId;
import com.intel.stl.datamanager.GroupInfoRecord;
import com.intel.stl.datamanager.PortConfigId;
import com.intel.stl.datamanager.PortConfigRecord;
import com.intel.stl.datamanager.SubnetRecord;
import com.intel.stl.datamanager.TopologyRecord;
import com.intel.stl.dbengine.DatabaseContext;
import com.intel.stl.dbengine.GroupDAO;

public class GroupDAOImpl extends BaseDAO implements GroupDAO {
    private static Logger log = LoggerFactory.getLogger("org.hibernate.SQL");

    protected static int BATCH_SIZE = 1000;

    public GroupDAOImpl(EntityManager entityManager) {
        super(entityManager);
    }

    public GroupDAOImpl(EntityManager entityManager, DatabaseContext databaseCtx) {
        super(entityManager, databaseCtx);
    }

    /**
     * 
     * Create GroupConfigRecord and save it to database.
     * 
     * Even for each default group(Switches, routers, HCAs, nodes), this will be
     * invoked. For default group, the List<PortConfigBean> will be null.
     * However, we just want to treat this as a general group.
     * 
     * 
     */
    @Override
    public GroupConfigRecord saveGroupConfig(SubnetRecord subnet,
            String groupName, List<PortConfigBean> ports) {
        GroupConfigRecord newGroupConfig = null;
        newGroupConfig = createGroupConfig(subnet, groupName, ports);

        return newGroupConfig;
    }

    @Override
    public void saveGroupList(SubnetRecord subnet, List<GroupListBean> groupList) {
        int numGroups = groupList.size();
        // String[] groups = groupList.getGroupNames();
        List<GroupConfigId> saveList = new ArrayList<GroupConfigId>();
        for (int i = 0; i < numGroups; i++) {
            GroupConfigId groupConfigId = new GroupConfigId();
            groupConfigId.setFabricId(subnet.getId());
            groupConfigId.setSubnetGroup(groupList.get(i).getGroupName());
            GroupConfigRecord groupConfig =
                    em.find(GroupConfigRecord.class, groupConfigId);
            if (groupConfig == null) {
                saveList.add(groupConfigId);
            }
        }
        if (saveList.size() > 0) {
            StringBuffer keys = new StringBuffer();
            keys.append(subnet.getSubnetDescription().getName());
            char separator = '|';
            startTransaction();
            for (GroupConfigId id : saveList) {
                GroupConfigRecord groupConfigRec = new GroupConfigRecord();
                groupConfigRec.setId(id);
                keys.append(separator);
                keys.append(id.getSubnetGroup());
                separator = ',';
                em.persist(groupConfigRec);
            }
            try {
                commitTransaction();
            } catch (Exception e) {
                throw createPersistDatabaseException(e,
                        GroupConfigRecord.class, keys);
            }
        }

    }

    /**
     * 
     * Description: Populate the GROUPS table. All Primary keys and non null
     * columns should be populated.
     * 
     * 
     * @param subnetRec
     * @param groupName
     * @param ports
     * @return
     * @throws DatabaseException
     */
    private GroupConfigRecord createGroupConfig(SubnetRecord subnet,
            String groupName, List<PortConfigBean> ports) {
        // Create an object of GroupConfigRecord and set non null fields before
        // trying to save it to DB.
        GroupConfigRecord groupConfigRec = new GroupConfigRecord();
        GroupConfigId groupConfigId = new GroupConfigId();
        groupConfigId.setFabricId(subnet.getId());
        groupConfigId.setSubnetGroup(groupName);
        groupConfigRec.setId(groupConfigId);

        startTransaction();
        em.persist(groupConfigRec);
        // Create the list of the PortConfigRecord from the list of the
        // PortConfigBean
        // and save it to DB.
        persistGroupPorts(groupConfigRec, ports);

        try {
            commitTransaction();
        } catch (Exception e) {
            throw createPersistDatabaseException(e, GroupConfigRecord.class,
                    groupName);
        }
        return groupConfigRec;
    }

    /**
     * 
     * Description: Populate the GROUPS_NODES_PORTS table. All Primary keys and
     * non null columns should be populated.
     * 
     * @param groupConfig
     * @param ports
     * @throws DatabaseException
     */
    private void persistGroupPorts(GroupConfigRecord groupConfig,
            List<PortConfigBean> ports) {
        long updates = 0;

        // From PortConfigBean get nodeGUID, port number and pass them to
        // PortConfigRecord.
        if (ports != null) {
            for (int i = 0; i < ports.size(); i++) {
                PortConfigBean portConfigBean = ports.get(i);

                PortConfigRecord portConfigRec = new PortConfigRecord();
                PortConfigId portConfigId = new PortConfigId();

                // Populate all primary and non null columns.
                portConfigId.setGroupId(groupConfig.getId());
                portConfigId.setNodeGUID(portConfigBean.getNodeGUID());
                portConfigId.setPortNumber(portConfigBean.getPortNumber());

                portConfigRec.setId(portConfigId);
                portConfigRec.setGroupConfig(groupConfig);

                em.persist(portConfigRec);
                updates++;
                if (updates >= BATCH_SIZE) {
                    flush();
                    clear();
                    updates = 0;
                }
            }
            if (updates > 0) {
                flush();
                clear();
            }
        }

    }

    /**
     * Retrieve the list of PortConfigBean from joins among TOPOLOGIES_NODES,
     * NODES and GROUP_NODES_PORTS database tables with topology ID and node
     * GUID.
     * 
     */
    @Override
    public List<PortConfigBean> getPortConfig(SubnetRecord subnetRec)
            throws PerformanceDataNotFoundException {

        TopologyRecord topology = subnetRec.getTopology();
        long topologyId = topology.getId();

        TypedQuery<PortConfigBean> query =
                em.createNamedQuery("PortConfigBean.findByTopId",
                        PortConfigBean.class);
        query.setParameter("topologyId", topologyId);
        List<PortConfigBean> portConfigBean = query.getResultList();
        if (portConfigBean == null || portConfigBean.size() == 0) {
            throw createPortConfigNotFoundException(topologyId);
        }
        return portConfigBean;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.dbengine.GroupDAO#getGroupConfig(com.intel.stl.datamanager
     * .GroupConfigId)
     */
    @Override
    public List<PortConfigBean> getGroupConfig(GroupConfigId groupConfigId)
            throws PerformanceDataNotFoundException {
        long subnetId = groupConfigId.getFabricId();
        String groupName = groupConfigId.getSubnetGroup();
        TypedQuery<PortConfigBean> query =
                em.createNamedQuery("PortConfigBean.findByGroupName",
                        PortConfigBean.class);
        query.setParameter("subnetId", subnetId);
        query.setParameter("groupName", groupName);
        List<PortConfigBean> portConfigBean = query.getResultList();
        if (portConfigBean == null || portConfigBean.size() == 0) {
            GroupConfigRecord rec =
                    em.find(GroupConfigRecord.class, groupConfigId);
            if (rec == null) {
                throw createGroupConfigNotFoundException(
                        groupConfigId.getFabricId(),
                        groupConfigId.getSubnetGroup());
            } else {
                portConfigBean = Collections.emptyList();
            }
        }
        return portConfigBean;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.dbengine.GroupDAO#saveGroupInfo(java.lang.String,
     * java.lang.String, com.intel.stl.datamanager.GroupConfigRecord)
     */
    @Override
    public void saveGroupInfos(SubnetRecord subnet,
            List<GroupInfoBean> groupInfoBeans) {
        StringBuffer keys = new StringBuffer();
        keys.append(subnet.getSubnetDescription().getName());
        char separator = '|';

        startTransaction();
        for (GroupInfoBean groupInfo : groupInfoBeans) {
            GroupInfoRecord groupInfoRec = createGroupInfo(subnet, groupInfo);
            GroupInfoRecord dbGroupInfo =
                    em.find(GroupInfoRecord.class, groupInfoRec.getId());
            if (dbGroupInfo == null) {
                em.persist(groupInfoRec);
                keys.append(separator);
                keys.append(groupInfoRec.getId().getGroupID().getSubnetGroup());
                keys.append('-');
                keys.append(groupInfoRec.getGroupInfo().getTimestamp());
                separator = ',';
            }
        }

        try {
            commitTransaction();
        } catch (Exception e) {
            throw createPersistDatabaseException(e, GroupInfoRecord.class, keys);
        }
    }

    /**
     * Description: Populate the GROUPS_INFOS table. All Primary keys and non
     * null columns should be populated.
     * 
     * @param subnetName
     * @param groupName
     * @return
     * @throws DatabaseException
     */
    private GroupInfoRecord createGroupInfo(SubnetRecord subnet,
            GroupInfoBean groupInfo) {
        // Create an object of GroupConfigRecord and set non null fields before
        // trying to save it to DB.
        GroupInfoRecord groupInfoRec = new GroupInfoRecord();
        GroupInfoId groupInfoId = new GroupInfoId();

        groupInfoId.setSweepTimestamp(groupInfo.getTimestamp());

        GroupConfigId groupConfigId = new GroupConfigId();
        groupConfigId.setFabricId(subnet.getId());
        groupConfigId.setSubnetGroup(groupInfo.getGroupName());

        groupInfoId.setGroupID(groupConfigId);

        // Don't need the following. GroupInfoBean includes all non null column
        // data.
        // However, note in the GroupDAOImplTest that the GROUPS table need to
        // have an entry for this
        // groupConfig before being able to persist this GroupInfoRecord because
        // it's a foreign key.
        // GroupConfigRecord groupConfigRec =
        // getGroupConfigWithException(groupConfigId);
        // groupInfoRec.setGroupConfig(groupConfigRec);

        groupInfoRec.setId(groupInfoId);
        groupInfoRec.setGroupInfo(groupInfo);

        return groupInfoRec;
    }

    /**
     * Retrieve group info for a specific time span.
     * 
     */
    @Override
    public List<GroupInfoBean> getGroupInfoList(SubnetRecord subnet,
            String groupName, long startTime, long stopTime)
            throws PerformanceDataNotFoundException {
        TypedQuery<GroupInfoBean> query =
                em.createNamedQuery("GroupInfoBean.findByTime",
                        GroupInfoBean.class);
        query.setParameter("subnetId", subnet.getId());
        query.setParameter("groupName", groupName);
        query.setParameter("startTime", startTime);
        query.setParameter("stopTime", stopTime);
        List<GroupInfoBean> groupInfoBeans = query.getResultList();

        if (groupInfoBeans == null || groupInfoBeans.size() == 0) {
            throw createGroupInfoNotFoundException(subnet
                    .getSubnetDescription().getName(), groupName, startTime,
                    stopTime);
        }
        return groupInfoBeans;

    }

    private DatabaseException createPersistDatabaseException(Throwable cause,
            Class<?> entityClass, Object entityId) {
        DatabaseException dbe =
                DatabaseUtils.createPersistDatabaseException(cause,
                        entityClass, entityId);
        log.error(dbe.getMessage(), cause);
        return dbe;
    }

    private PerformanceDataNotFoundException createPortConfigNotFoundException(
            Object... arguments) {
        PerformanceDataNotFoundException ge =
                new PerformanceDataNotFoundException(
                        STL30053_PORT_CONFIG_NOT_FOUND_SUBNET, arguments);
        return ge;
    }

    private PerformanceDataNotFoundException createGroupInfoNotFoundException(
            Object... arguments) {
        PerformanceDataNotFoundException pe =
                new PerformanceDataNotFoundException(
                        STL30054_GROUP_INFO_NOT_FOUND_TIME, arguments);
        return pe;
    }

    private PerformanceDataNotFoundException createGroupConfigNotFoundException(
            Object... arguments) {
        PerformanceDataNotFoundException ge =
                new PerformanceDataNotFoundException(
                        STL30045_GROUP_CONFIG_NOT_FOUND, arguments);
        return ge;
    }
}