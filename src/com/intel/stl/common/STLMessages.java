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
 *  File Name: STLMessages.java
 * 
 *  Archive Source: $Source$
 * 
 *  Archive Log: $Log$
 *  Archive Log: Revision 1.50  2015/09/21 20:46:56  jijunwan
 *  Archive Log: PR 130542 - Confusion error message on fetching conf file
 *  Archive Log: - improved SftpException to include file path information
 *  Archive Log:
 *  Archive Log: Revision 1.49  2015/08/17 18:49:06  jijunwan
 *  Archive Log: PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log: - change backend files' headers
 *  Archive Log:
 *  Archive Log: Revision 1.48  2015/08/17 14:22:53  rjtierne
 *  Archive Log: PR 128979 - SM Log display
 *  Archive Log: This is the first version of the Log Viewer which displays select lines of text from the remote SM log file. Updates include searchable raw text from file, user-defined number of lines to display, refreshing end of file, and paging. This PR is now closed and further updates can be found by referencing PR 130011 - "Enhance SM Log Viewer to include Standard and Advanced requirements".
 *  Archive Log:
 *  Archive Log: Revision 1.47  2015/08/10 17:04:42  robertja
 *  Archive Log: PR128974 - Email notification functionality.
 *  Archive Log:
 *  Archive Log: Revision 1.46  2015/07/31 21:04:51  fernande
 *  Archive Log: PR 129631 - Ports table sometimes not getting performance related data. Translating a RequestCancelledException into a special PerformanceRequestCancelledException for UI consumption.
 *  Archive Log:
 *  Archive Log: Revision 1.45  2015/07/02 14:43:00  robertja
 *  Archive Log: PR 128703 - Messages for new failover states.
 *  Archive Log:
 *  Archive Log: Revision 1.44  2015/07/01 18:19:52  robertja
 *  Archive Log: PR128703 - Add messages for new failover states.
 *  Archive Log:
 *  Archive Log: Revision 1.43  2015/06/18 20:55:51  fernande
 *  Archive Log: PR 129034 Support secure FE. Improvements to framework in SMFailoverManager
 *  Archive Log:
 *  Archive Log: Revision 1.42  2015/06/11 17:47:11  fernande
 *  Archive Log: PR 129034 Support secure FE. Added message for SMRecordBean record not found condition.
 *  Archive Log:
 *  Archive Log: Revision 1.41  2015/06/10 19:36:45  jijunwan
 *  Archive Log: PR 129153 - Some old files have no proper file header. They cannot record change logs.
 *  Archive Log: - wrote a tool to check and insert file header
 *  Archive Log: - applied on backend files
 *  Archive Log:
 * 
 *  Overview:
 * 
 *  @author: Fernando Fernandez
 * 
 ******************************************************************************/

package com.intel.stl.common;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.IMessage;

/**
 * This is the message repository for the Fabric Manager GUI messages. Please,
 * follow the following convention: STL10001-STL10999: Initialization and
 * general messages STL20001-STL20999: Messages related to the Connect Manager
 * component STL30001-STL30999: Messages related to the Database Manager
 * component Add ranges as more components are added. Don't forget to add the
 * actual message in the messages.properties file.
 * 
 * @author Fernando Fernandez
 * 
 */
public enum STLMessages implements IMessage {

    STL10001_SETTINGS_FILE_FORMAT_INVALID(10001),
    STL10002_IO_EXCEPTION_READING_SETTINGS(10002),
    STL10003_FOLDER_CANNOT_BE_CREATED(10003),
    STL10004_SECURITY_EXCEPTION_IN_FOLDER(10004),
    STL10005_DATABASE_ENGINE_NOTSUPPORTED(10005),
    STL10006_ERROR_STARTING_DATABASE_ENGINE(10006),
    STL10007_CONFIGURATION_OPTION_NOT_SET(10007),
    STL10008_FOLDER_DOES_NOT_EXIST(10008),
    STL10009_ERROR_READING_FILE(10009),
    STL10010_ERROR_DURING_INIT(10010),
    STL10011_ANNOTATED_CLASS_NOT_FOUND(10011),
    STL10012_PERSISTENCE_UNIT_NOT_FOUND(10012),
    STL10013_ERROR_LOADING_PERSISTENCE_XML(10013),
    STL10014_CANNOT_OVERRIDE_SETTING(10014),
    STL10015_OVERRIDING_SETTING(10015),
    STL10016_INITIALIZING_COMPONENT_REGISTRY(10016),
    STL10017_CHECKING_MULTI_APP_INSTANCES(10017),
    STL10018_DATABASE_COMPONENT(10018),
    STL10019_MAIL_COMPONENT(10019),
    STL10020_APPCONTEXT_COMPONENT(10020),
    STL10021_STARTING_ASYNC_TASKS(10021),
    STL10022_ERROR_IN_ASYNC_TASK_CREATION(10022),
    STL10023_ERROR_READING_RESOURCE(10023),
    STL10024_NO_COMPONENT_FOUND(10024),
    STL10025_STARTING_COMPONENT(10025),
    STL10026_FE_ADAPTER(10026),

    STL10100_ERRORS_INIT_APP(10100),
    STL10101_ONE_ERROR_INIT_APP(10101),
    STL10102_MULTI_INSTANCES(10102),

    // FEC driver errors
    STL20000_MAD_FAILED(20000),
    STL20001_CONNECTION_ERROR(20001),
    STL20002_CONNECTION_CLOSED(20002),
    STL20003_CONNECTION_TIMEOUT(20003),
    STL20004_SM_UNAVAILABLE(20004),
    STL20005_PM_UNAVAILABLE(20005),
    STL20006_CHANNEL_CLOSED(20006),

    // Database Manager messages
    STL30001_STARTING_DATABASE_ENGINE(30001),
    STL30002_DATABASE_ENGINE_ERROR(30002),
    STL30003_DATABASE_DEFINITION_FILE_NOT_FOUND(30003),
    STL30004_ERROR_READING_DATABASE_DEFINITION(30004),
    STL30005_ERRORS_DURING_DATABASE_DEFINITION(30005),
    STL30006_SQLEXCEPTION(30006),
    STL30007_ERROR_STARTING_DB_ENGINE(30007),
    STL30008_DATABASE_DEFINITION_TIMESTAMP(30008),
    STL30009_COULD_NOT_WRITE_SCHEMA_TIMESTAMP(30009),
    STL30010_STARTING_SCHEMA_UPDATE(30010),
    STL30011_ERROR_UPDATING_SCHEMA(30011),
    STL30012_ENTITY_NOT_FOUND(30012),
    STL30013_ERROR_SAVING_ENTITY(30013),
    STL30014_DATABASE_CALL_EXECUTION_EXCEPTION(30014),
    STL30015_DATABASE_CALL_INTERRUPTION_EXCEPTION(30015),
    STL30016_ENGINE_NOT_STARTED(30016),
    STL30017_NODE_WITH_DUPLICATE_LID(30017),
    STL30018_NODE_NOT_FOUND_WITH_LID(30018),
    STL30020_ENTITY_MANAGER_CLOSED(30020),
    STL30021_DATABASE_SERVER_NOT_INITIALIZED(30021),
    // DB Subnet DAO
    STL30022_SUBNET_NOT_FOUND(30022),
    STL30023_NODE_NOT_FOUND(30023),
    STL30024_LINK_NOT_FOUND(30024),
    STL30025_TOPOLOGY_NOT_FOUND(30025),
    STL30026_USER_NOT_FOUND(30026),
    // DB Subnet API
    STL30027_NODE_NOT_FOUND_SUBNET(30027),
    STL30028_LINK_NOT_FOUND_SUBNET(30028),
    STL30029_LINK_NOT_FOUND_TOPOLOGY(30029),
    STL30030_LINK_NOT_FOUND_TNF(30030),
    STL30031_NODE_NOT_FOUND_LID(30031),
    STL30032_NODE_NOT_FOUND_TNF(30032),
    STL30033_NODE_NOT_FOUND_PORT_GUID(30033),
    STL30034_LINK_NOT_FOUND_NNF_SOURCE(30034),
    STL30035_LINK_NOT_FOUND_LNF_SOURCE(30035),
    STL30036_LINK_NOT_FOUND_NNF_DESTINATION(30036),
    STL30037_LINK_NOT_FOUND_LNF_DESTINATION(30037),
    // DB Config API
    STL30038_ERROR_GETTING_SUBNETS(30038),
    STL30039_ERROR_GETTING_USER(30039),
    STL30040_ERROR_SAVING_USER(30040),
    STL30041_ERROR_SAVING_SUBNET(30041),
    STL30042_ERROR_GETTING_EVENT(30042),
    STL30043_ERROR_SAVING_EVENT(30043),
    STL30044_EVENT_NOT_FOUND(30044),
    // DB Group DAO
    STL30045_GROUP_CONFIG_NOT_FOUND(30045),
    STL30046_PORT_CONFIG_NOT_FOUND(30046),
    STL30047_GROUP_INFO_NOT_FOUND(30047),
    // DB Group API
    STL30048_ERROR_SAVING_GROUP_CONFIG(30048),
    STL30049_ERROR_GETTING_GROUP_CONFIG(30049),
    STL30050_ERROR_GETTING_PORT_CONFIG(30050),
    STL30051_ERROR_SAVING_GROUP_INFO(30051),
    STL30052_ERROR_GETTING_GROUP_INFO(30052),
    STL30053_PORT_CONFIG_NOT_FOUND_SUBNET(30053),
    STL30054_GROUP_INFO_NOT_FOUND_TIME(30054),
    STL30055_NODE_NOT_FOUND_IN_CACHE_LID(30055),
    STL30056_NODE_NOT_FOUND_IN_CACHE_PORT_GUID(30056),
    STL30057_NODE_TYPE_DIST_FOUND_IN_CACHE(30057),
    STL30058_LINK_NOT_FOUND_CACHE_ALL(30058),
    STL30059_LINK_NOT_FOUND_CACHE_SOURCE(30059),
    STL30060_LINK_NOT_FOUND_CACHE_DESTINATION(30060),
    STL30061_NODE_NOT_FOUND_CACHE_ALL(30061),
    STL30062_PORT_NOT_FOUND_CACHE_ALL(30062),
    STL30063_PORT_NOT_FOUND_CACHE(30063),
    STL30064_PORT_NOT_FOUND_CACHE_LOCAL(30064),
    // Notice Mgr
    STL30065_INCONSISTENT_DATA_NODE_PORT(30065),
    STL30066_INCONSISTENT_DATA_NODE_LINK(30066),
    STL30067_ERROR_GETTING_NOTICES(30067),
    STL30068_TRANSACTION_ACTIVE(30068),
    STL30069_NO_IMAGEINFO(30069),
    STL30070_IMAGE_NUMBER_NOT_FOUND(30070),
    STL30071_INVALID_USEROPTIONS_XML(30071),

    // Cable Info
    STL30072_CABLE_NOT_FOUND_CACHE_ALL(30072),

    STL30073_NO_CACHE_FOUND(30073),

    // Subnet Management messages
    STL40001_ERROR_No_DATA(40001),
    STL40002_FABRIC_EXECUTIVE_DRIVER_ERROR(40002),
    STL40003_PARAMETERS(40003),
    STL40004_DATABASE_ERROR_SUBNET(40004),
    STL40005_DATABASE_ERROR_CONFIG(40005),
    STL40006_SMRECORD_NOT_FOUND(40006),

    // Configuration API messages
    STL50001_ERROR_PARSING_LOGGING_CONFIG(50001),
    STL50002_ERROR_READING_LOG4JPROPERTIES(50002),
    STL50003_ERROR_CLOSING_LOG4JPROPERTIES(50003),
    STL50004_UNSUPPORTED_APPENDER_TYPE(50004),
    STL50005_ERROR_UPDATING_LOGGING_CONFIG(50005),
    STL50006_UNKNOWN_LOG4J_SETTING(50006),
    STL50007_ERROR_UPDATING_CUSTOMSETTINGS(50007),
    STL50008_SUBNET_CONNECTION_ERROR(50008),
    STL50009_SUBNET_CONTEXT_NOT_CREATED(50009),
    STL50010_ASYNC_SERVICE_NOT_INITIALIZED(50010),
    STL50011_INVALID_XPATH_EXPRESSION(50011),
    STL50012_SOCKET_CLOSE_FAILURE(50012),
    STL50013_ERROR_PARSING_FM_CONFIG(50013),

    // General messages
    STL60001_FATAL_FAILURE(60001),
    STL60002_SUBNET_DATA_FAILURE(60002),
    STL60003_PERFORMANCE_DATA_FAILURE(60003),
    STL60004_ARGUMENT_CANNOT_BE_NULL(60004),
    STL60005_EXCEPTION_EXECUTING_TASK(60005),
    STL60006_EXCEPTION_REFRESHING_CACHE(60006),
    STL60007_SUBMITTERS_TRACE(60007),
    STL60008_REQUEST_CANCELLED_BY_USER(60008),

    STL61001_CERT_CONF(61001),
    STL61002_KEY_STORE_LOC(61002),
    STL61003_KEY_STORE_PWD(61003),
    STL61004_TRUST_STORE_LOC(61004),
    STL61005_TRUST_STORE_PWD(61005),
    STL61006_CERT_PWD(61006),
    STL61007_CERT_ERR(61007),
    STL61008_SSL_HANDSHAKE_COMPLETE(61008),
    STL61010_LOGIN(61010),
    STL61011_HOST(61011),
    STL61012_USER_NAME(61012),
    STL61013_PASSWORD(61013),
    STL61014_PORT(61014),
    STL61015_CONNECTING(61015),
    STL61016_FETCHING(61016),
    STL61017_DEPLOYING(61017),
    STL61018_LOG_FILE_NOT_FOUND(61018),
    STL61019_SSH_CONNECTION_FAILURE(61019),
    STL61020_SFTP_FAILURE(61020),

    STL61100_REF_CONFLICT(61100),
    STL61101_DUP_NAME(61101),

    STL62001_NOTIFY_ERR(62001),
    STL62002_NOTIFY_SUBJECT(62002),

    STL63001_GET_APPS_ERR(63001),
    STL63002_ADD_APP_ERR(63002),
    STL63003_REMOVE_APP_ERR(63003),
    STL63004_UPDATE_APP_ERR(63004),
    STL63005_ADDUPDATE_APP_ERR(63005),
    STL63006_GET_APP_ERR(63006),

    STL63011_GET_DGS_ERR(63011),
    STL63012_ADD_DG_ERR(63012),
    STL63013_REMOVE_DG_ERR(63013),
    STL63014_UPDATE_DG_ERR(63014),
    STL63015_ADDUPDATE_DG_ERR(63015),
    STL63016_GET_DG_ERR(63016),

    STL63021_GET_VFS_ERR(63021),
    STL63022_ADD_VF_ERR(63022),
    STL63023_REMOVE_VF_ERR(63023),
    STL63024_UPDATE_VF_ERR(63024),
    STL63025_ADDUPDATE_VF_ERR(63025),
    STL63026_GET_VF_ERR(63026),

    // Failover messages.
    STL64000_SM_FAILOVER_UNSUCCESSFUL(64000),
    STL64001_SM_FAILOVER_COMPLETE(64001),
    STL64002_SM_FAILOVER_CONNECTION_ATTEMPT(64002),
    STL64003_SM_FAILOVER_CONNECTION_FAILED(64003),
    STL64004_SM_FAILOVER_CONNECTION_RETRY(64004),
    STL64005_SM_FAILOVER_ATTEMPTING_CONNECTION(64005),
    STL64006_SM_FAILOVER_GET_SM_ERROR(64006),
    STL64007_SM_FAILOVER_GET_PM_ERROR(64007),
    STL64008_SM_FAILOVER_GET_SM_RETRY(64008),
    STL64009_SM_FAILOVER_GET_PM_RETRY(64009),

    STL70000_SMTP_UNABLE_TO_CONNECT(70000),

    STL99999_HOLDER(99999);

    private static final String STL_MESSAGES_BUNDLE =
            "com.intel.stl.common.messages";

    private static final String STL_MESSAGES_ENCODING = "UTF-8";

    private static final Control STL_CONTROL = new UTFControl(
            STL_MESSAGES_ENCODING);

    private static final ResourceBundle STL_MESSAGES = ResourceBundle
            .getBundle(STL_MESSAGES_BUNDLE, STL_CONTROL);

    private static Logger log = LoggerFactory.getLogger(STLMessages.class);

    private final int errorcode;

    private final String key;

    private STLMessages(int errorcode) {
        this.errorcode = errorcode;
        this.key = String.format("STL%05d", errorcode);
    }

    @Override
    public int getErrorCode() {
        return errorcode;
    }

    @Override
    public String getMessageKey() {
        return key;
    }

    @Override
    public String getDescription() {
        try {
            return STL_MESSAGES.getString(key);
        } catch (MissingResourceException mre) {
            String message = "Message '" + key + "' not found!";
            log.error(message);
            return message;
        }
    }

    @Override
    public String getDescription(Object... arguments) {
        try {
            return MessageFormat.format(STL_MESSAGES.getString(key), arguments);
        } catch (MissingResourceException mre) {
            String message = "Message '" + key + "' not found!";
            log.error(message);
            return message;
        }
    }
}
