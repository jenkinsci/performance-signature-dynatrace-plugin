/*
 * Copyright (c) 2014 T-Systems Multimedia Solutions GmbH
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

package de.tsystems.mms.apm.performancesignature.dynatrace.model;

import com.google.gson.annotations.SerializedName;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.Date;

/**
 * Alert record description
 */
@ApiModel(description = "Alert record description")

@ExportedBean
public class Alert {
    @SerializedName("severity")
    private SeverityEnum severity;
    @SerializedName("state")
    private EventStateEnum state;
    @SerializedName("message")
    private String message;
    @SerializedName("description")
    private String description;
    @SerializedName("start")
    private Date start;
    @SerializedName("end")
    private Date end;
    @SerializedName("rule")
    private final String rule = null;
    @SerializedName("systemprofile")
    private String systemprofile;

    /**
     * The severity of the alert
     *
     * @return severity
     **/
    @Exported
    @ApiModelProperty(value = "The severity of the alert")
    public SeverityEnum getSeverity() {
        return severity;
    }

    /**
     * The state of the alert
     *
     * @return state
     **/
    @Exported
    @ApiModelProperty(value = "The state of the alert")
    public EventStateEnum getState() {
        return state;
    }

    /**
     * Get message
     *
     * @return message
     **/
    @Exported
    @ApiModelProperty(required = true)
    public String getMessage() {
        return message;
    }

    /**
     * Get description
     *
     * @return description
     **/
    @Exported
    public String getDescription() {
        return description;
    }

    /**
     * Start time in ISO 8601 compatible date/time of format: yyyy-MM-dd&#39;T&#39;HH:mm:ss.SSSXXX
     *
     * @return start
     **/
    @Exported
    @ApiModelProperty(example = "2016-05-11T11:35:31.170+02:00", value = "Start time in ISO 8601 compatible date/time of format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public Date getStart() {
        return start == null ? null : (Date) start.clone();
    }

    /**
     * End time in ISO 8601 compatible date/time of format: yyyy-MM-dd&#39;T&#39;HH:mm:ss.SSSXXX
     *
     * @return end
     **/
    @Exported
    @ApiModelProperty(example = "2016-05-11T11:35:31.170+02:00", value = "End time in ISO 8601 compatible date/time of format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public Date getEnd() {
        return end == null ? null : (Date) end.clone();
    }

    /**
     * Incident Rule name
     *
     * @return rule
     **/
    @Exported
    @ApiModelProperty(required = true, value = "Incident Rule name")
    public String getRule() {
        return rule;
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

    public String getPanelColor() {
        switch (severity) {
            case INFORMATIONAL:
                return "";
            case WARNING:
                return "panel-warning";
            case SEVERE:
                return "panel-danger";
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Alert {\n");

        sb.append("    severity: ").append(PerfSigUIUtils.toIndentedString(severity)).append("\n");
        sb.append("    state: ").append(PerfSigUIUtils.toIndentedString(state)).append("\n");
        sb.append("    message: ").append(PerfSigUIUtils.toIndentedString(message)).append("\n");
        sb.append("    description: ").append(PerfSigUIUtils.toIndentedString(description)).append("\n");
        sb.append("    start: ").append(PerfSigUIUtils.toIndentedString(start)).append("\n");
        sb.append("    end: ").append(PerfSigUIUtils.toIndentedString(end)).append("\n");
        sb.append("    rule: ").append(PerfSigUIUtils.toIndentedString(rule)).append("\n");
        sb.append("    systemprofile: ").append(PerfSigUIUtils.toIndentedString(systemprofile)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
