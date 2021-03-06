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
 *  File Name: LogHelper2.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.7  2015/10/06 15:50:35  rjtierne
 *  Archive Log:    PR 130390 - Windows FM GUI - Admin tab->Logs side-tab - unable to login to switch SM for log access
 *  Archive Log:    - Changed the behavior of the Log Viewer when logging into an ESM. Instead of restricting login to user name
 *  Archive Log:    "root" only, now disabling the Low Viewer and displaying a message since there is no SM Log in an ESM.
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2015/09/29 15:30:24  rjtierne
 *  Archive Log:    PR 130332 - windows FM GUI - Admin-Logs - when logging in it displays error message about NULL log
 *  Archive Log:    - Reorganized initializationTask() to initialize the log file path
 *  Archive Log:    - Now checking for existence of a log file regardless of whether it is user defined in the config file
 *  Archive Log:    or the default file.
 *  Archive Log:    - Only issuing an error if no log file can be found
 *  Archive Log:    - Removed call to display error when using the default log file
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2015/09/25 13:53:35  rjtierne
 *  Archive Log:    PR 130011 - Enhance SM Log Viewer to include Standard and Advanced requirements
 *  Archive Log:    - Prevent non-root users from logging in to view the log
 *  Archive Log:    - Implemented setStartLine() and setEndLine()
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2015/09/10 20:56:49  jijunwan
 *  Archive Log:    PR 130409 - [Dell]: FMGUI Admin Console login fails when switch is configured without username and password
 *  Archive Log:    - improved code to better handle conf file not found
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2015/08/18 21:31:34  jijunwan
 *  Archive Log:    PR 128979 - SM Log display
 *  Archive Log:    - checked in Rick's code that fixed currentLine calculation issues
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2015/08/17 18:48:54  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - change backend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/08/17 14:22:52  rjtierne
 *  Archive Log:    PR 128979 - SM Log display
 *  Archive Log:    This is the first version of the Log Viewer which displays select lines of text from the remote SM log file. Updates include searchable raw text from file, user-defined number of lines to display, refreshing end of file, and paging. This PR is now closed and further updates can be found by referencing PR 130011 - "Enhance SM Log Viewer to include Standard and Advanced requirements".
 *  Archive Log:
 *
 *  Overview: Over-arching helper class to the LogApi, to submit commands to
 *  the LogCommandProcessor, and process responses for the UI
 *
 *  @author: rjtierne
 *
 ******************************************************************************/

package com.intel.stl.api.logs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.SubnetContext;
import com.intel.stl.api.management.FMConfHelper;
import com.intel.stl.api.subnet.HostType;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.fecdriver.network.ssh.impl.JSchSession;
import com.intel.stl.fecdriver.network.ssh.impl.JSchSessionFactory;

public class LogHelper implements IResponseListener, ILogErrorListener,
        ILogPageListener {

    private final static Logger log = LoggerFactory.getLogger(LogHelper.class);

    public final String DEFAULT_LOG_FILE = "/var/log/messages";

    private final boolean DEBUG_LOG = false;

    private final static int RESPONSE_TIMEOUT = 10000; // 10 seconds

    private boolean usingDefaultLogFile = false;

    private final LogHelper helper = this;

    private boolean logRunning = false;

    private ILogStateListener logStateListener;

    private final SubnetDescription subnet;

    private final FMConfHelper fmConfigHelper;

    private final FMConfigParser fmConfigParser;

    private LogStatusTask logStatusTask;

    private LogErrorType errorCode;

    private LogCommandProcessor userCommandProcessor;

    private final LogCommander logCommander;

    private FileInfoBean fileInfo;

    private String logFilePath;

    private String userLogFilePath;

    private JSchSession jschSession;

    private boolean initInProgress;

    public LogHelper(SubnetContext subnetContext) {
        super();
        this.subnet = subnetContext.getSubnetDescription();
        fmConfigHelper = FMConfHelper.getInstance(subnet);
        fmConfigParser = new FMConfigParser(fmConfigHelper);
        fileInfo = new FileInfoBean(DEFAULT_LOG_FILE, 0, 0, 0);
        this.logCommander = new LogCommander(fileInfo);
        logCommander.setPageMonitorListener(this);
    }

    protected void initializationTask(final SubnetDescription subnet,
            final boolean strictHostKey, final char[] password) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    initInProgress = true;
                    errorCode = LogErrorType.LOG_OK;

                    // Start the SSH session
                    errorCode = initializeSsh(subnet, strictHostKey, password);

                    if (errorCode == LogErrorType.LOG_OK) {
                        // Initialize the User Command Processor
                        userCommandProcessor =
                                new LogCommandProcessor(jschSession,
                                        RESPONSE_TIMEOUT, helper.getClass()
                                                .getSimpleName());
                        userCommandProcessor.setResponseListener(helper);
                        userCommandProcessor.setErrorListener(helper);

                        // Initialize the log file path
                        errorCode = initLogFilePath(password);
                        if (errorCode.equals(LogErrorType.ESM_NOT_SUPPORTED)) {
                            logStateListener.onEsmHost(errorCode, logFilePath);
                        }
                    } else {
                        logStateListener.onError(errorCode, subnet.getName());
                    }
                } catch (Exception e) {
                    logStateListener.onError(
                            LogErrorType.UNEXPECTED_LOGIN_FAILURE,
                            e.getMessage());
                    stopLog();
                }
            }
        }).start();
    }

    protected LogErrorType initLogFilePath(final char[] password) {

        LogErrorType errorCode = LogErrorType.LOG_OK;

        // Retrieve the log file name from FMConfig
        try {
            logFilePath = fmConfigParser.getLogFilePath(password);

            // No Log Viewer support for ESMs
            if (subnet.getPrimaryFE().getHostType().equals(HostType.ESM)) {
                return LogErrorType.ESM_NOT_SUPPORTED;
            }

            // If there is no logfile path in the config
            // file, check if the default file exists
            if (logFilePath == null) {
                System.out.println("logFilePath=" + logFilePath);
                logFilePath = DEFAULT_LOG_FILE;
                System.out.println("...using default:" + DEFAULT_LOG_FILE);
            } else {
                // Capture user defined log file path in case it can't be found
                userLogFilePath = logFilePath;
            }

            if (DEBUG_LOG) {
                // Override the SM Log file
                logFilePath = "/nfs/site/home/rjtierne/bin/messages";
            }

            // Initialize the file info bean
            fileInfo.setFileName(logFilePath);

            // Check if the log file exists
            checkForFile();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return errorCode;
    }

    protected void startLogStatusTask(long delay, long timeBetweenExecutions) {
        // Initialize the timer task to retrieve total # lines
        logStatusTask =
                new LogStatusTask(logFilePath, LogMessageType.NUM_LINES,
                        jschSession);
        logStatusTask.setResponseListener(helper);
        logStatusTask.setErrorListener(helper);
        logStatusTask.start(delay, timeBetweenExecutions);
    }

    protected void debug(String... msgs) {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        System.out.print(formatter.format(new Date()) + ": ");
        for (int i = 0; i < msgs.length; i++) {
            System.out.print(msgs[i]);
            if ((i > 0) && (i % msgs.length) != 0) {
                System.out.print(", ");
            } else {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    public String getLogFileName() {
        return fileInfo.getFileName();
    }

    public long getFileSize() {
        return fileInfo.getFileSize();
    }

    protected synchronized String getScriptCmd(LogMessageType msgType,
            long numLinesRequested) {

        logCommander.setFileInfo(fileInfo);
        String cmd = logCommander.getCommand(msgType, numLinesRequested);

        return cmd;
    }

    public void getNumLines() {
        String cmd = getScriptCmd(LogMessageType.NUM_LINES, 0);
        userCommandProcessor.submitCommand(LogMessageType.NUM_LINES, cmd);
    }

    public long getCurrentLine() {
        return fileInfo.getCurrentLine();
    }

    public long getTotalLines() {
        return fileInfo.getTotalNumLines();
    }

    public void checkForFile() {
        String cmd = getScriptCmd(LogMessageType.CHECK_FOR_FILE, 0);
        userCommandProcessor.submitCommand(LogMessageType.CHECK_FOR_FILE, cmd);
    }

    public synchronized void schedulePreviousPage(long numLinesRequested) {
        String cmd =
                getScriptCmd(LogMessageType.PREVIOUS_PAGE, numLinesRequested);
        userCommandProcessor.submitCommand(LogMessageType.PREVIOUS_PAGE, cmd);
    }

    public synchronized void scheduleNextPage(long numLinesRequested) {
        String cmd = getScriptCmd(LogMessageType.NEXT_PAGE, numLinesRequested);
        userCommandProcessor.submitCommand(LogMessageType.NEXT_PAGE, cmd);
    }

    public synchronized void scheduleLastLines(long numLinesRequested) {
        String cmd = getScriptCmd(LogMessageType.LAST_LINES, numLinesRequested);
        userCommandProcessor.submitCommand(LogMessageType.LAST_LINES, cmd);
    }

    public void setLogStateListener(ILogStateListener listener) {
        logStateListener = listener;
    }

    public void setLogCheckInterval(long intervalInMs) {

    }

    protected LogErrorType initializeSsh(SubnetDescription subnet,
            boolean strictHostKey, char[] password) throws Exception {

        LogErrorType error = LogErrorType.LOG_OK;

        // Initialize the session and the exec channel
        try {
            jschSession =
                    JSchSessionFactory.getSession(subnet, strictHostKey,
                            password);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if ((jschSession == null) || (!jschSession.isConnected())) {
                error = LogErrorType.SSH_HOST_CONNECT_ERROR;
            }
        }

        return error;
    }

    public boolean isRunning() {
        return logRunning;
    }

    public boolean hasSession(SubnetDescription subnet) {
        boolean connectionStatus = false;

        // Check if the factory has a session for this subnet
        // and if it does, verify that it is connected
        JSchSession session = JSchSessionFactory.getSessionFromMap(subnet);

        if (session != null) {
            connectionStatus = session.isConnected();
        }

        return connectionStatus;
    }

    protected void onFinish(LogErrorType errorCode, Object... data) {
        if (errorCode.getId() == LogErrorType.LOG_OK.getId()) {
            logRunning = true;
            logStateListener.onReady();

            // Start the file status timer task
            startLogStatusTask(0, 10000);
        } else {
            logRunning = false;
            logStateListener.onError(errorCode, data);
            stopLog();
        }
    }

    public void startLog(SubnetDescription subnet, boolean strictHostKey,
            char[] password) {

        if (!logRunning) {
            debug("Start Log...");
            initializationTask(subnet, strictHostKey, password);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.IErrorListener#stopLog()
     */
    @Override
    public void stopLog() {
        if (logRunning) {
            try {
                if (userCommandProcessor != null) {
                    try {
                        userCommandProcessor.shutdown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (logStatusTask != null) {
                    try {
                        logStatusTask.shutdown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                logRunning = false;
            }
            debug("Log Stopped...");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.logs.ICommandListener#onResponseReceived(LogResponse)
     */
    @Override
    public synchronized void onResponseReceived(LogResponse response) {

        switch (response.getMsgType()) {
            case CHECK_FOR_FILE:
                String fileName = response.getEntries().get(0);
                if (fileName.contains(logFilePath)) {
                    logStateListener.onResponse(response);
                    onFinish(errorCode, logFilePath);
                }
                break;

            case PREVIOUS_PAGE:
            case NEXT_PAGE:
            case FILE_SIZE:
            case LAST_LINES:
                logStateListener.onResponse(response);
                break;

            case NUM_LINES:
                // Get # lines from the LogStatusTask and store in FileInfoBean
                long totalNumLines =
                        Long.parseLong(response.getEntries().get(0));
                if (response.getEntries().size() > 0) {
                    fileInfo.setTotalNumLines(totalNumLines);
                    if (initInProgress && (totalNumLines > 0)) {
                        fileInfo.setCurrentLine(totalNumLines);
                        initInProgress = false;
                    }
                    logStateListener.onResponse(response);
                }
                break;

            case EXIT:
                break;

            case UNKNOWN:
                break;

            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.logs.IResponseListener#onResponseError(LogErrorType,
     * LogMessageType)
     */
    @Override
    public synchronized void onResponseError(LogErrorType errorCode,
            LogMessageType msgType) {

        if (msgType == LogMessageType.CHECK_FOR_FILE) {

            // If the default log file has already be specified, it must be
            // inaccessible
            if (usingDefaultLogFile) {
                logStateListener.onError(LogErrorType.LOG_FILE_NOT_FOUND,
                        userLogFilePath, DEFAULT_LOG_FILE);
                stopLog();
            } else {
                // Resend the command requesting the default log file
                logFilePath = DEFAULT_LOG_FILE;
                fileInfo = new FileInfoBean(logFilePath, 0, 0, 0);
                fileInfo.setFileName(logFilePath);
                usingDefaultLogFile = true;
                checkForFile();
            }
        } else {
            logStateListener.onError(errorCode, logFilePath);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogPageListener#setFirstPage(boolean)
     */
    @Override
    public void setFirstPage(boolean b) {
        logStateListener.setFirstPage(b);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogPageListener#setLastPage(boolean)
     */
    @Override
    public void setLastPage(boolean b) {
        logStateListener.setLastPage(b);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.IErrorListener#onSessionDown(String)
     */
    @Override
    public void onSessionDown(String errorMessage) {
        logStateListener.onSessionDown(errorMessage);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogPageListener#setStartLine(long)
     */
    @Override
    public void setStartLine(long lineNum) {
        logStateListener.setStartLine(lineNum);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogPageListener#setEndLine(long)
     */
    @Override
    public void setEndLine(long lineNum) {
        logStateListener.setEndLine(lineNum);
    }
}
