/*
 * Copyright (c) 2014-2018 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model;

import com.google.gson.annotations.SerializedName;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import static de.tsystems.mms.apm.performancesignature.dynatrace.model.Alert.SeverityEnum;
import static de.tsystems.mms.apm.performancesignature.dynatrace.model.Alert.StateEnum;

/**
 * Deployment event record description
 */
@ApiModel(description = "Deployment event record description")

public class DeploymentEvent {
    @SerializedName("message")
    private final String message;
    @SerializedName("systemprofile")
    private final String systemprofile;
    @SerializedName("severity")
    private SeverityEnum severity;
    @SerializedName("state")
    private StateEnum state;
    @SerializedName("description")
    private String description;
    @SerializedName("start")
    private Date start;
    @SerializedName("end")
    private Date end;
    @SerializedName("application")
    private String application;

    public DeploymentEvent(final String systemprofile, final String message) {
        this.systemprofile = systemprofile;
        this.message = message;
    }

    /**
     * The severity of the event
     *
     * @return severity
     **/
    @ApiModelProperty(value = "The severity of the event")
    public SeverityEnum getSeverity() {
        return severity;
    }

    public DeploymentEvent setSeverity(final SeverityEnum severity) {
        this.severity = severity;
        return this;
    }

    /**
     * The state of the event
     *
     * @return state
     **/
    @ApiModelProperty(value = "The state of the event")
    public StateEnum getState() {
        return state;
    }

    public DeploymentEvent setState(final StateEnum state) {
        this.state = state;
        return this;
    }

    /**
     * Get message
     *
     * @return message
     **/
    @ApiModelProperty(required = true)
    public String getMessage() {
        return message;
    }

    /**
     * Get description
     *
     * @return description
     **/

    public String getDescription() {
        return description;
    }

    public DeploymentEvent setDescription(final String description) {
        this.description = description;
        return this;
    }

    /**
     * Start time in ISO 8601 compatible date/time of format: yyyy-MM-dd&#39;T&#39;HH:mm:ss.SSSXXX
     *
     * @return start
     **/
    @ApiModelProperty(example = "2016-05-11T11:35:31.170+02:00", value = "Start time in ISO 8601 compatible date/time of format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public Date getStart() {
        return start == null ? null : (Date) start.clone();
    }

    public DeploymentEvent setStart(final Date start) {
        this.start = start != null ? (Date) start.clone() : null;
        return this;
    }

    /**
     * End time in ISO 8601 compatible date/time of format: yyyy-MM-dd&#39;T&#39;HH:mm:ss.SSSXXX
     *
     * @return end
     **/
    @ApiModelProperty(example = "2016-05-11T11:35:31.170+02:00", value = "End time in ISO 8601 compatible date/time of format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public Date getEnd() {
        return end == null ? null : (Date) end.clone();
    }

    public DeploymentEvent setEnd(final Date end) {
        this.end = end != null ? (Date) end.clone() : null;
        return this;
    }

    /**
     * System Profile name
     *
     * @return systemprofile
     **/
    @ApiModelProperty(required = true, value = "System Profile name")
    public String getSystemprofile() {
        return systemprofile;
    }

    /**
     * Application name
     *
     * @return application
     **/
    @ApiModelProperty(value = "Application name")
    public String getApplication() {
        return application;
    }

    public DeploymentEvent setApplication(final String application) {
        this.application = application;
        return this;
    }

    @Override
    public String toString() {
        return "class DeploymentEvent {\n" +
                "    severity: " + PerfSigUIUtils.toIndentedString(severity) + "\n" +
                "    state: " + PerfSigUIUtils.toIndentedString(state) + "\n" +
                "    message: " + PerfSigUIUtils.toIndentedString(message) + "\n" +
                "    description: " + PerfSigUIUtils.toIndentedString(description) + "\n" +
                "    start: " + PerfSigUIUtils.toIndentedString(start) + "\n" +
                "    end: " + PerfSigUIUtils.toIndentedString(end) + "\n" +
                "    systemprofile: " + PerfSigUIUtils.toIndentedString(systemprofile) + "\n" +
                "    application: " + PerfSigUIUtils.toIndentedString(application) + "\n" +
                "}";
    }
}
