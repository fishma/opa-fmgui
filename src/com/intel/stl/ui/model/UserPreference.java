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
 *  File Name: UserPreference.java
 *
 *  Archive Source: $Source$
 *
 *  Archive Log:    $Log$
 *  Archive Log:    Revision 1.7  2015/09/25 20:53:37  fernande
 *  Archive Log:    PR129920 - revisit health score calculation. Changed formula to include several factors (or attributes) within the calculation as well as user-defined weights (for now are hard coded).
 *  Archive Log:
 *  Archive Log:    Revision 1.6  2015/09/14 19:04:33  jijunwan
 *  Archive Log:    PR 130229 - The text component of all editable combo boxes should provide validation of the input
 *  Archive Log:    - added exception check just in case
 *  Archive Log:
 *  Archive Log:    Revision 1.5  2015/08/17 18:53:46  jijunwan
 *  Archive Log:    PR 129983 - Need to change file header's copyright text to BSD license txt
 *  Archive Log:    - changed frontend files' headers
 *  Archive Log:
 *  Archive Log:    Revision 1.4  2015/04/02 17:03:59  jypak
 *  Archive Log:    Null checks added.
 *  Archive Log:
 *  Archive Log:    Revision 1.3  2015/02/23 15:05:36  rjtierne
 *  Archive Log:    Changed getRefreshRateUnit() to change the property string to upper
 *  Archive Log:    case before getting the TimeUnit value
 *  Archive Log:
 *  Archive Log:    Revision 1.2  2015/02/20 21:12:14  rjtierne
 *  Archive Log:    Fixed typo
 *  Archive Log:
 *  Archive Log:    Revision 1.1  2015/02/10 23:11:55  jijunwan
 *  Archive Log:    1) changed task scheduler to support initial refresh rate
 *  Archive Log:    2) improved refresh rate update handling
 *  Archive Log:
 *
 *  Overview: 
 *
 *  @author: jijunwan
 *
 ******************************************************************************/

package com.intel.stl.ui.model;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.intel.stl.api.configuration.UserSettings;

/**
 * Helper class that wraps Properties and provides easy access to user
 * preferences. In the future when we change backend as a general way to store
 * user data, and it's frontend's responsibility to generate and parser data,
 * this class can be the main class to do the job.
 */
public class UserPreference {
    private final Properties properties;

    /**
     * Description:
     * 
     * @param properties
     */
    public UserPreference(UserSettings userSettings) {
        Properties userPrefs = new Properties();
        if (userSettings != null) {
            userPrefs = userSettings.getUserPreference();
        }
        this.properties = userPrefs;
    }

    public UserPreference(Properties userPrefs) {
        this.properties = userPrefs;
    }

    public int getRefreshRate() {
        return getInt(UserSettings.PROPERTY_REFRESH_RATE);
    }

    public TimeUnit getRefreshRateUnit() {
        String name = UserSettings.PROPERTY_REFRESH_RATE_UNITS;
        String str = properties.getProperty(name);
        if (str != null) {
            return TimeUnit.valueOf(str.toUpperCase());
        } else {
            throw new IllegalArgumentException("Couldn't find property for '"
                    + name + "'");
        }
    }

    public int getRefreshRateInSeconds() {
        int rate = getRefreshRate();
        TimeUnit unit = getRefreshRateUnit();
        return (int) TimeUnit.SECONDS.convert(rate, unit);
    }

    public int getTimeWindowInSeconds() {
        return getInt(UserSettings.PROPERTY_TIMING_WINDOW);
    }

    public int getNumWorstNodes() {
        return getInt(UserSettings.PROPERTY_NUM_WORST_NODES);
    }

    public int getWeightForHealthScoreAttribute(HealthScoreAttribute attribute) {
        int weight = 0;
        String str = properties.getProperty(attribute.getPropertiesKey());
        if (str != null) {
            try {
                weight = Integer.valueOf(str);
            } catch (NumberFormatException nfe) {
                weight = attribute.getDefaultWeight();
            }
        } else {
            weight = attribute.getDefaultWeight();
        }
        return weight;
    }

    private int getInt(String name) {
        String str = properties.getProperty(name);
        if (str != null) {
            try {
                return Integer.valueOf(str);
            } catch (NumberFormatException nfe) {
                // shouldn't happen
                nfe.printStackTrace();
                return -1;
            }
        } else {
            throw new IllegalArgumentException("Counldn't find property for '"
                    + name + "'");
        }
    }
}
