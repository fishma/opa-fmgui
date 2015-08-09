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
 *  File Name: ChartsCard.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.4  2014/09/18 14:59:33  jijunwan
 *  Archive Log:    Added jumping to destination support to TopN chart via popup menu
 *  Archive Log:    Added label highlight for chart view
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/09/15 15:24:31  jijunwan
 *  Archive Log:    changed AppEventBus to 3rd party lib mbassador
 *  Archive Log:    some code reformat
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/07/22 18:38:42  jijunwan
 *  Archive Log:    introduced DatasetDescription to support short name and full name (description) for a dataset
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/07/21 17:03:06  jijunwan
 *  Archive Log:    moved ChartsView and ChartsCard to common package
 *  Archive Log:
 *  Archive Log:    Revision 1.7  2014/07/16 20:54:19  jijunwan
 *  Archive Log:    fixed port link
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2014/07/11 19:23:23  fernande
 *  Archive Log:    Adding event bus and linking from UI elements to the Performance tab
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2014/05/09 14:17:14  jijunwan
 *  Archive Log:    moved JFreeChart to view side, controller side only take care dataset
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2014/05/08 19:25:36  jijunwan
 *  Archive Log:    MVC refactory
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2014/04/30 14:56:46  rjtierne
 *  Archive Log:    Changes to reflect renamed JCard and ICard
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/04/16 16:20:43  jijunwan
 *  Archive Log:    minor refactory
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/04/12 19:50:38  fernande
 *  Archive Log:    Initial version
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2014/04/08 17:32:56  jijunwan
 *  Archive Log:    introduced new summary section for "Home Page"
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2014/04/03 20:52:18  jijunwan
 *  Archive Log:    on going work on "Home" page
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.ui.common;

import java.util.List;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.ui.common.view.ChartsView;
import com.intel.stl.ui.common.view.IChartsCardListener;
import com.intel.stl.ui.event.JumpDestination;
import com.intel.stl.ui.event.PortSelectedEvent;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.model.DatasetDescription;
import com.intel.stl.ui.model.PortEntry;

public class ChartsCard extends
        BaseCardController<IChartsCardListener, ChartsView> implements
        IChartsCardListener {
    private String currentChart;

    public ChartsCard(ChartsView view, MBassador<IAppEvent> eventBus,
            List<DatasetDescription> datasets) {
        super(view, eventBus);
        view.setDatasets(datasets);
        if (datasets != null && !datasets.isEmpty()) {
            currentChart = datasets.get(0).getName();
        }
        view.setChart(currentChart);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.main.view.IChartsCardListener#onSelectChart(int)
     */
    @Override
    public void onSelectChart(String name) {
        currentChart = name;
        view.setChart(currentChart);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.BaseCardController#getCardListener()
     */
    @Override
    public IChartsCardListener getCardListener() {
        return this;
    }

    @Override
    public void jumpTo(Object content, JumpDestination destination) {
        if (content instanceof PortEntry) {
            PortEntry pe = (PortEntry) content;
            PortSelectedEvent event =
                    new PortSelectedEvent(pe.getNodeLid(), pe.getPortNum(),
                            this, destination);
            eventBus.publish(event);
        }
    }
}