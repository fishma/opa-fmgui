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
 *  File Name: MailNotifier.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.2  2015/08/17 18:53:35  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - changed frontend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/08/10 17:30:51  robertja
 *  Archive Log:    PR 128974 - Email notification functionality.
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: robertja
 *
 ******************************************************************************/

package com.intel.stl.ui.alert;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.notice.EventDescription;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.main.Context;

public class MailNotifier extends NoticeNotifier {
    private static Logger log = LoggerFactory.getLogger(MailNotifier.class);

    private static boolean DEBUG = true;

    private final List<String> recipients = new ArrayList<String>();

    private final String subnetName;

    /**
     * Description:
     * 
     * @param context
     * @param rules
     * @param recipients
     */
    public MailNotifier(Context context, List<INotifyRule> rules,
            List<String> recipients) {
        super(context, rules);
        log.debug("MailNotifier: constructor called.");
        this.subnetName = context.getSubnetDescription().getName();
        setRecipients(recipients);
    }

    public synchronized void setRecipients(List<String> recipients) {
        if (recipients != null) {
            this.recipients.clear();
            this.recipients.addAll(recipients);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.notice.notify.NoticeNotifier#notify(java.util.List)
     */
    @Override
    protected synchronized void notify(List<EventDescription> toSend) {
        log.debug("MailNotifier: notify called.");
        if (toSend == null || toSend.isEmpty()) {
            return;
        }

        for (EventDescription event : toSend) {
            // For each event, create a message subject and body
            // and send an email to each recipient.

            String subject = createMessageSubject(event);
            String body = createMessageBody(event);

            context.getConfigurationApi().submitMessage(subject, body,
                    recipients);
        }

    }

    protected String createMessageSubject(EventDescription event) {
        String subject =
                UILabels.STL92000_EMAIL_SUBJECT.getDescription(context
                        .getSubnetDescription().getName(), event.getSeverity());
        return subject;
    }

    protected String createMessageBody(EventDescription event) {
        return event.toString();
    }

}
