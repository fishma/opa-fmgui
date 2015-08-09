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
package com.intel.stl.ui.main;

import static com.intel.stl.ui.common.PageWeight.MEDIUM;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.api.configuration.EventType;
import com.intel.stl.api.notice.EventDescription;
import com.intel.stl.api.notice.FESource;
import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.performance.ImageInfoBean;
import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.SMRecordBean;
import com.intel.stl.ui.common.EventTableModel;
import com.intel.stl.ui.common.IPageController;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.ISectionController;
import com.intel.stl.ui.common.PageWeight;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.view.EventTableView;
import com.intel.stl.ui.common.view.ISectionListener;
import com.intel.stl.ui.common.view.JSectionView;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.view.HomeView;
import com.intel.stl.ui.main.view.PerformanceSectionView;
import com.intel.stl.ui.main.view.SummarySectionViewOld;
import com.intel.stl.ui.model.GroupStatistics;
import com.intel.stl.ui.model.StateSummary;
import com.intel.stl.ui.publisher.CallbackAdapter;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;
import com.intel.stl.ui.publisher.TaskScheduler;
import com.intel.stl.ui.publisher.subscriber.ImageInfoSubscriber;
import com.intel.stl.ui.publisher.subscriber.SubscriberType;

/**
 * @author jijunwan
 * 
 */
public class HomePageOld implements IPageController {
    private TaskScheduler scheduler;

    private ISubnetApi subnetApi;

    private HomeView view = null;

    private final List<ISectionController<?>> sections;

    private SummarySectionOld summary;

    private PerformanceSection performance;

    private EventTableSection events;

    private GroupStatistics groupStatistics;

    private final boolean showEvents;

    private Task<ImageInfoBean> imageInfoTask;

    private ICallback<ImageInfoBean> imageInfoCallback;

    private Task<StateSummary> stateSummaryTask;

    @SuppressWarnings("unused")
    private ICallback<StateSummary> stateSummaryCallback;

    private int mPortNum = 0;

    private final MBassador<IAppEvent> eventBus;

    private ImageInfoSubscriber imageInfoSubscriber;

    public HomePageOld(MBassador<IAppEvent> eventBus) {
        this(eventBus, false);
    }

    public HomePageOld(MBassador<IAppEvent> eventBus, boolean showEvents) {
        this.showEvents = showEvents;
        this.eventBus = eventBus;
        view = new HomeView();
        sections = getSections();
        List<JSectionView<?>> sectionViews = new ArrayList<JSectionView<?>>();
        for (ISectionController<?> section : sections) {
            sectionViews.add(section.getView());
        }
        view.installSectionViews(sectionViews);
    }

    protected List<ISectionController<?>> getSections() {
        List<ISectionController<?>> sections =
                new ArrayList<ISectionController<?>>();

        summary = new SummarySectionOld(new SummarySectionViewOld(), eventBus);
        sections.add(summary);

        performance =
                new PerformanceSection(new PerformanceSectionView(), eventBus);
        sections.add(performance);

        if (showEvents) {
            final EventTableModel tableModel = new EventTableModel();
            final EventTableView tableView = new EventTableView(tableModel);
            JSectionView<ISectionListener> eventsView =
                    new JSectionView<ISectionListener>(
                            STLConstants.K0104_HOME_EVENTS.getValue()) {
                        private static final long serialVersionUID =
                                -3095494060522455280L;

                        @Override
                        protected JComponent getMainComponent() {
                            return tableView;
                        }
                    };

            events =
                    new EventTableSection(tableModel, eventsView, tableView,
                            eventBus);
            sections.add(events);
        }

        return sections;
    }

    /**
     * @param context
     *            the context to set
     */
    @Override
    public void setContext(Context context, IProgressObserver observer) {
        IProgressObserver[] subObservers = observer.createSubObservers(2);

        subnetApi = context.getSubnetApi();
        scheduler = context.getTaskScheduler();
        imageInfoSubscriber =
                (ImageInfoSubscriber) scheduler
                        .getSubscriber(SubscriberType.IMAGE_INFO);

        imageInfoCallback = new CallbackAdapter<ImageInfoBean>() {
            /*
             * (non-Javadoc)
             * 
             * @see
             * com.intel.hpc.stl.ui.publisher.CallBackAdapter#onDone(java.lang
             * .Object)
             */
            @Override
            public synchronized void onDone(ImageInfoBean result) {
                if (result != null) {
                    processImageInfo(result);
                }
            }
        };
        imageInfoTask =
                imageInfoSubscriber.registerImageInfo(imageInfoCallback);
        if (observer.isCancelled()) {
            clear();
            return;
        }

        stateSummaryCallback = new CallbackAdapter<StateSummary>() {
            /*
             * (non-Javadoc)
             * 
             * @see
             * com.intel.hpc.stl.ui.publisher.CallBackAdapter#onDone(java.lang
             * .Object)
             */
            @Override
            public synchronized void onDone(StateSummary result) {
                if (result != null) {
                    processStateSummary(result);
                }
            }
        };
        // stateSummaryTask =
        // scheduler.registerStateSummary(rate, stateSummaryCallback);
        if (observer.isCancelled()) {
            clear();
            return;
        }
        subObservers[0].onFinish();

        performance.setContext(context, subObservers[1]);
        observer.onFinish();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.common.IPageController#onRefresh(com.intel.stl.ui.common
     * .IProgressObserver)
     */
    @Override
    public void onRefresh(IProgressObserver observer) {
        viewClear();
        IPerformanceApi perfApi = scheduler.getPerformanceApi();

        ImageInfoBean imgInfo = perfApi.getLatestImageInfo();
        imageInfoCallback.onDone(imgInfo);
        if (observer.isCancelled()) {
            viewClear();
            return;
        }

        // StateSummary ss = scheduler.getStateSummary();
        // stateSummaryCallback.onDone(ss);
        if (observer.isCancelled()) {
            viewClear();
            return;
        }

        performance.onRefresh(observer);
        observer.onFinish();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#clear()
     */
    @Override
    public void clear() {
        viewClear();

        if (scheduler != null) {
            if (imageInfoTask != null) {
                imageInfoSubscriber.deregisterImageInfo(imageInfoTask,
                        imageInfoCallback);
            }
            if (stateSummaryTask != null) {
                // scheduler.deregisterStateSummary(stateSummaryTask,
                // stateSummaryCallback);
            }
        }

        groupStatistics = null;
    }

    protected void viewClear() {
        for (ISectionController<?> section : sections) {
            section.clear();
        }
    }

    protected void processImageInfo(ImageInfoBean imageInfo) {
        synchronized (this) {
            if (groupStatistics == null) {
                groupStatistics =
                        new GroupStatistics(
                                subnetApi.getConnectionDescription(), imageInfo);
                try {
                    groupStatistics.setNodeTypesDist(subnetApi
                            .getNodesTypeDist(false));
                    groupStatistics.setPortTypesDist(subnetApi
                            .getPortsTypeDist(true));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                groupStatistics.setImageInfo(imageInfo);
            }
            int msmLid = imageInfo.getSMInfo()[0].getLid();
            SMRecordBean msm = subnetApi.getSM(msmLid);
            if (msm != null) {
                groupStatistics.setMsmUptimeInSeconds(msm.getSmInfo()
                        .getElapsedTime());
            }
        }
        summary.updateStatistics(groupStatistics);
    }

    protected void processStateSummary(StateSummary stateSummary) {
        if (groupStatistics == null) {
            return;
        }

        int totalSWs = 0;
        int totalCAs = 0;
        synchronized (this) {
            totalSWs = groupStatistics.getNodeTypesDist().get(NodeType.SWITCH);
            totalCAs = groupStatistics.getNodeTypesDist().get(NodeType.HFI);
        }
        summary.updateStates(stateSummary.getSwitchStates(), totalSWs,
                stateSummary.getHfiStates(), totalCAs);
        summary.updateHealthScore(stateSummary.getHealthScore());
        summary.updateWorstNodes(stateSummary.getWorstNodes());
    }

    protected void updateEventTable() {
        if (events == null) {
            return;
        }

        EventDescription event = new EventDescription();
        event.setDate(new Date());
        event.setSeverity(NoticeSeverity.INFO);

        FESource feSrc = new FESource();
        feSrc.setHost("FDR_SWITCH9");
        event.setType(EventType.FE_CONNECTION_LOST);

        if (mPortNum < 100) {
            mPortNum++;
            feSrc.setPort(mPortNum);
            event.setSource(feSrc);
            events.updateTable(event);
        } // if
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.ui.IPage#getName()
     */
    @Override
    public String getName() {
        return "Home 2";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.ui.IPage#getDescription()
     */
    @Override
    public String getDescription() {
        return STLConstants.K0101_HOME_DESCRIPTION.getValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.ui.IPage#getView()
     */
    @Override
    public JPanel getView() {
        return view;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.ui.IPage#getIcon()
     */
    @Override
    public ImageIcon getIcon() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPage#cleanup()
     */
    @Override
    public void cleanup() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#onEnter()
     */
    @Override
    public void onEnter() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#onExit()
     */
    @Override
    public void onExit() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#canExit()
     */
    @Override
    public boolean canExit() {
        return true;
    }

    @Override
    public PageWeight getContextSwitchWeight() {
        return MEDIUM;
    }

    @Override
    public PageWeight getRefreshWeight() {
        return MEDIUM;
    }
}