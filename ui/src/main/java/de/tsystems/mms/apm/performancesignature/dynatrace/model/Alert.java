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

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;
import java.util.Arrays;
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
    private StateEnum state;
    @SerializedName("message")
    private String message;
    @SerializedName("description")
    private String description;
    @SerializedName("start")
    private Date start;
    @SerializedName("end")
    private Date end;
    @SerializedName("rule")
    private String rule;

    public Alert() {
    }

    public Alert(SeverityEnum severity, String message, String description, Long timeframeStart, Long timeframeStop, String rule) {
        this();
        this.severity = severity;
        this.message = message;
        this.description = description;
        this.start = new Date(timeframeStart);
        this.end = new Date(timeframeStop);
        this.rule = rule;
    }

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
    public StateEnum getState() {
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

    @Override
    public String toString() {
        return "class Alert {\n" +
                "    severity: " + PerfSigUIUtils.toIndentedString(severity) + "\n" +
                "    state: " + PerfSigUIUtils.toIndentedString(state) + "\n" +
                "    message: " + PerfSigUIUtils.toIndentedString(message) + "\n" +
                "    description: " + PerfSigUIUtils.toIndentedString(description) + "\n" +
                "    start: " + PerfSigUIUtils.toIndentedString(start) + "\n" +
                "    end: " + PerfSigUIUtils.toIndentedString(end) + "\n" +
                "    rule: " + PerfSigUIUtils.toIndentedString(rule) + "\n" +
                "}";
    }

    /**
     * The severity of the alert
     */
    @JsonAdapter(SeverityEnum.Adapter.class)
    public enum SeverityEnum {
        INFORMATIONAL("informational"),
        WARNING("warning"),
        SEVERE("severe");

        private final String value;

        SeverityEnum(String value) {
            this.value = value;
        }

        public static SeverityEnum fromValue(String text) {
            return Arrays.stream(SeverityEnum.values()).filter(b -> b.value.equals(text)).findFirst().orElse(null);
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

        public String getPanelColor() {
            switch (fromValue(value)) {
                case WARNING:
                    return "panel-warning";
                case SEVERE:
                    return "panel-danger";
                default:
                    return "";
            }
        }

        public static class Adapter extends TypeAdapter<SeverityEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final SeverityEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public SeverityEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return SeverityEnum.fromValue(value);
            }
        }
    }

    /**
     * The state of the alert
     */
    @JsonAdapter(StateEnum.Adapter.class)
    public enum StateEnum {
        CREATED("Created"),
        INPROGRESS("InProgress"),
        CONFIRMED("Confirmed");

        private final String value;

        StateEnum(String value) {
            this.value = value;
        }

        public static StateEnum fromValue(String text) {
            return Arrays.stream(StateEnum.values()).filter(b -> b.value.equals(text)).findFirst().orElse(null);
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static class Adapter extends TypeAdapter<StateEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final StateEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public StateEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return StateEnum.fromValue(value);
            }
        }
    }
}
