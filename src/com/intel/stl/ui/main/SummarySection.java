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
 *  Functional Group: FabricViewer
 *
 *  File Name: SummarySection.java
 *
 *  Archive Source: 
 *
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.ui.main;

import java.util.Date;
import java.util.EnumMap;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.ui.common.BaseSectionController;
import com.intel.stl.ui.common.ICardController;
import com.intel.stl.ui.common.view.ISectionListener;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.view.SummarySectionView;
import com.intel.stl.ui.model.GroupStatistics;
import com.intel.stl.ui.model.NodeScore;
import com.intel.stl.ui.model.TimedScore;

/**
 * @author jijunwan
 * 
 */
public class SummarySection extends
        BaseSectionController<ISectionListener, SummarySectionView> {
    private final StatisticsCard statisticsCard;

    private final StatusCard statusCard;

    private final HealthHistoryCard healthHistoryCard;

    private final WorstNodesCard worstNodesCard;

    public SummarySection(SummarySectionView view, MBassador<IAppEvent> eventBus) {
        super(view, eventBus);
        statisticsCard = new StatisticsCard(view.getStatisticsView(), eventBus);
        statusCard = new StatusCard(view.getStatusView(), eventBus);
        healthHistoryCard =
                new HealthHistoryCard(view.getHealthView(), eventBus);
        worstNodesCard = new WorstNodesCard(view.getWorstNodesView(), eventBus);

        HelpAction helpAction = HelpAction.getInstance();
        helpAction.getHelpBroker().enableHelpOnButton(view.getHelpButton(),
                helpAction.getSubnetSummary(), helpAction.getHelpSet());
    }

    /**
     * @return the staticticsCard
     */
    public StatisticsCard getStatisticsCard() {
        return statisticsCard;
    }

    /**
     * @return the swStatesCard
     */
    public StatusCard getStatusCard() {
        return statusCard;
    }

    /**
     * @return the healthHistoryCard
     */
    public HealthHistoryCard getHealthHistoryCard() {
        return healthHistoryCard;
    }

    /**
     * @return the worstNodesCard
     */
    public WorstNodesCard getWorstNodesCard() {
        return worstNodesCard;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.ui.ISection#getCards()
     */
    @Override
    public ICardController<?>[] getCards() {
        return new ICardController[] { statisticsCard, statusCard,
                healthHistoryCard, worstNodesCard };
    }

    /**
     * @param groupStatistics
     */
    public void updateStatistics(GroupStatistics groupStatistics) {
        statisticsCard.updateStatistics(groupStatistics);
    }

    /**
     * @param swStates
     * @param totalSWs
     * @param caStates
     * @param totalCAs
     */
    public void updateStates(EnumMap<NoticeSeverity, Integer> swStates,
            int totalSWs, EnumMap<NoticeSeverity, Integer> caStates,
            int totalCAs) {
        statusCard.updateSwStates(swStates, totalSWs);
        statusCard.updateFiStates(caStates, totalCAs);
    }

    /**
     * @param score
     * @param time
     */
    public void updateHealthScore(TimedScore score) {
        if (score != null) {
            healthHistoryCard.updateHealthScore(score.getScore(), new Date(
                    score.getTime()));
        }
    }

    /**
     * @param nodes
     */
    public void updateWorstNodes(NodeScore[] nodes) {
        worstNodesCard.updateWorstNodes(nodes);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.ISection#clear()
     */
    @Override
    public void clear() {
        statisticsCard.clear();
        statusCard.clear();
        healthHistoryCard.clear();
        worstNodesCard.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.BaseSectionController#getSectionListener()
     */
    @Override
    protected ISectionListener getSectionListener() {
        return this;
    }

    // Push latest context to Health History Card to give it access
    // to latest refresh rate.
    public void setContext(Context context) {
        healthHistoryCard.updateContext(context);
    }
}