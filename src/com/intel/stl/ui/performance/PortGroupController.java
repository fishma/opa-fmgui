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
 *  File Name: PortGroupController.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.3  2015/08/17 18:53:49  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - changed frontend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2015/06/30 22:31:42  jijunwan
 *  Archive Log:    PR 129215 - Need short chart name to support pin capability
 *  Archive Log:    - use short name as pin card title
 *  Archive Log:    - improved pin argument to include full name and provide data source description
 *  Archive Log:    - fixed improper full name issues on trend charts
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/06/25 16:23:32  jijunwan
 *  Archive Log:    Bug 126755 - Pin Board functionality is not working in FV
 *  Archive Log:    - designed and implemented pin framework
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.ui.performance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.engio.mbassy.bus.MBassador;

import org.jfree.data.time.TimeSeriesCollection;

import com.intel.stl.ui.common.ChartsCard;
import com.intel.stl.ui.common.view.ChartsView;
import com.intel.stl.ui.common.view.IChartCreator;
import com.intel.stl.ui.common.view.OptionChartsView;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.model.DatasetDescription;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.monitor.ChartScaleGroupManager;
import com.intel.stl.ui.monitor.DataRateChartScaleGroupManager;
import com.intel.stl.ui.monitor.PacketRateChartScaleGroupManager;
import com.intel.stl.ui.monitor.view.TrafficChartsCreator;
import com.intel.stl.ui.performance.item.IPerformanceItem;
import com.intel.stl.ui.performance.item.PortCountersItem;

public class PortGroupController extends
        AbstractGroupController<PortSourceName> {
    private ChartsCard rxCard;

    private ChartsCard txCard;

    private final PacketRateChartScaleGroupManager packetScaleManager;

    private final DataRateChartScaleGroupManager dataScaleManager;

    @SuppressWarnings("unchecked")
    public PortGroupController(MBassador<IAppEvent> eventBus, String name,
            PortCountersItem rxPktItem, PortCountersItem rxDataItem,
            PortCountersItem txPktItem, PortCountersItem txDataItem,
            HistoryType[] historyTypes) {
        super(eventBus, name, new IPerformanceItem[] { rxPktItem, rxDataItem,
                txPktItem, txDataItem });
        installTimeScopes(historyTypes);

        packetScaleManager = PacketRateChartScaleGroupManager.getInstance();
        if (rxCard != null) {
            packetScaleManager.addChart(
                    rxCard.getView().getChart(rxPktItem.getName()),
                    (TimeSeriesCollection) rxPktItem.getDataset());
        }
        if (txCard != null) {
            packetScaleManager.addChart(
                    txCard.getView().getChart(txPktItem.getName()),
                    (TimeSeriesCollection) txPktItem.getDataset());
        }
        rxPktItem.setScaleManager(packetScaleManager);
        txPktItem.setScaleManager(packetScaleManager);

        dataScaleManager = DataRateChartScaleGroupManager.getInstance();
        if (rxCard != null) {
            dataScaleManager.addChart(
                    rxCard.getView().getChart(rxDataItem.getName()),
                    (TimeSeriesCollection) rxDataItem.getDataset());
        }
        if (txCard != null) {
            dataScaleManager.addChart(
                    txCard.getView().getChart(txDataItem.getName()),
                    (TimeSeriesCollection) txDataItem.getDataset());
        }
        rxDataItem.setScaleManager(dataScaleManager);
        txDataItem.setScaleManager(dataScaleManager);
    }

    protected void installTimeScopes(HistoryType... types) {
        if (group != null) {
            ChartsView view = group.getChartView();
            if (view instanceof OptionChartsView) {
                ((OptionChartsView) view).setHistoryTypes(types);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.performance.AbstractGroupController#initCards(java.util
     * .Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected List<ChartsCard> initCards(Map<String, DatasetDescription> map) {
        List<ChartsCard> res = new ArrayList<ChartsCard>();
        rxCard =
                createCard(new IPerformanceItem[] { allItems[0], allItems[1] },
                        map);
        res.add(rxCard);
        txCard =
                createCard(new IPerformanceItem[] { allItems[2], allItems[3] },
                        map);
        res.add(txCard);
        return res;
    }

    public void setHelpIDs(String rxHelpID, String txHelpID) {
        if (rxCard != null) {
            rxCard.setHelpID(rxHelpID);
        }
        if (txCard != null) {
            txCard.setHelpID(txHelpID);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.performance.AbstractGroupController#getItemView(com.
     * intel.stl.ui.performance.item.IPerformanceItem)
     */
    @Override
    protected ChartsView getItemView(IPerformanceItem<PortSourceName> item) {
        if (allItems[0] == item || allItems[1] == item) {
            return rxCard.getView();
        } else if (allItems[2] == item || allItems[3] == item) {
            return txCard.getView();
        } else {
            return null;
        }
    }

    /**
     * @return the rxCard
     */
    public ChartsCard getRxCard() {
        return rxCard;
    }

    /**
     * @return the txCard
     */
    public ChartsCard getTxCard() {
        return txCard;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.performance.AbstractGroupController#clearPin(com.intel
     * .stl.ui.common.ChartsCard,
     * com.intel.stl.ui.performance.item.IPerformanceItem)
     */
    @Override
    protected void clearPin(ChartsCard pinCard,
            IPerformanceItem<PortSourceName> pinItem) {
        super.clearPin(pinCard, pinItem);
        // remove from scale manager
        ChartScaleGroupManager<TimeSeriesCollection> scaleMgr =
                ((PortCountersItem) pinItem).getScaleManager();
        scaleMgr.removeChart(pinCard.getView().getChart(pinItem.getName()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.performance.AbstractGroupController#getPinCard(com.intel
     * .stl.ui.performance.ChartArgument,
     * com.intel.stl.ui.performance.item.IPerformanceItem)
     */
    @Override
    protected ChartsCard getPinCard(ChartArgument<PortSourceName> arg,
            IPerformanceItem<PortSourceName> source) {
        ChartsCard res = super.getPinCard(arg, source);
        // set scale manager
        PortCountersItem pinItem = (PortCountersItem) pinItems.get(arg);
        if (pinItem != null) {
            ChartScaleGroupManager<TimeSeriesCollection> scaleMgr =
                    pinItem.getScaleManager();
            if (scaleMgr == null) {
                if (source.getName().equals(allItems[0].getName())
                        || source.getName().equals(allItems[2].getName())) {
                    scaleMgr = packetScaleManager;
                } else if (source.getName().equals(allItems[1].getName())
                        || source.getName().equals(allItems[3].getName())) {
                    scaleMgr = dataScaleManager;
                }
                if (scaleMgr != null) {
                    scaleMgr.addChart(res.getView().getChart(arg.getName()),
                            (TimeSeriesCollection) pinItem.getDataset());
                    pinItem.setScaleManager(scaleMgr);
                    scaleMgr.updateChartsRange();
                }
            }
        }
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.performance.AbstractGroupController#getChartCreator()
     */
    @Override
    protected IChartCreator getChartCreator() {
        return TrafficChartsCreator.instance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.performance.AbstractGroupController#getChartArgument
     * (com.intel.stl.ui.performance.item.IPerformanceItem)
     */
    @Override
    protected ChartArgument<PortSourceName> getChartArgument(
            IPerformanceItem<PortSourceName> item) {
        PortChartArgument arg = new PortChartArgument();
        String name = item.getName();
        arg.setName(name);
        arg.setFullName(item.getFullName());
        arg.setProvider(item.getCurrentProviderName().name());
        if (item.getType() != null) {
            arg.setDataType(item.getType());
        }
        if (item.getHistoryType() != null) {
            arg.setHistoryType(item.getHistoryType());
        }
        PortSourceName[] sourceNames = item.getSources();
        if (sourceNames != null && sourceNames.length == 1) {
            arg.setSources(item.getSources());
        } else {
            // shouldn't happen
            throw new RuntimeException(
                    "Strange performance item with sources: "
                            + Arrays.toString(sourceNames));
        }
        return arg;
    }
}
