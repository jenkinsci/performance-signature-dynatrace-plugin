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

package de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.BaseReference;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SessionData.SessionTypeEnum;
import static de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SessionData.StoredSessiontypeEnum;

/**
 * Comprehensive metadata of a session
 */
@ApiModel(description = "Comprehensive metadata of a session")

public class SessionMetadata extends BaseReference {
    @SerializedName("storedsessiontype")
    private StoredSessiontypeEnum storedsessiontype;
    @SerializedName("sessiontype")
    private SessionTypeEnum sessiontype;
    @SerializedName("systemprofile")
    private String systemprofile;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("size")
    private Long size;
    @SerializedName("deletionlocked")
    private Boolean deletionlocked = false;
    @SerializedName("directorypath")
    private String directorypath;
    @SerializedName("state")
    private StateEnum state;
    @SerializedName("capturingstart")
    private Date capturingstart;
    @SerializedName("capturingduration")
    private Long capturingduration;
    @SerializedName("version")
    private String version;
    @SerializedName("recordingtype")
    private String recordingtype;
    @SerializedName("agent")
    private String agent;
    @SerializedName("numberofpurepaths")
    private Integer numberofpurepaths;
    @SerializedName("continuoussession")
    private Boolean continuoussession = false;
    @SerializedName("labels")
    private List<String> labels;

    /**
     * Stored session type
     *
     * @return storedsessiontype
     **/
    @ApiModelProperty(value = "Stored session type")
    public StoredSessiontypeEnum getStoredsessiontype() {
        return storedsessiontype;
    }

    /**
     * Session type
     *
     * @return sessiontype
     **/
    @ApiModelProperty(value = "Session type")
    public SessionTypeEnum getSessiontype() {
        return sessiontype;
    }

    /**
     * Name of the system profile the session belongs to
     *
     * @return systemprofile
     **/
    @ApiModelProperty(value = "Name of the system profile the session belongs to")
    public String getSystemprofile() {
        return systemprofile;
    }

    /**
     * Session name
     *
     * @return name
     **/
    @ApiModelProperty(value = "Session name")
    public String getName() {
        return name;
    }

    /**
     * Get description
     *
     * @return description
     **/

    public String getDescription() {
        return description;
    }

    /**
     * Size in bytes
     *
     * @return size
     **/
    @ApiModelProperty(value = "Size in bytes")
    public Long getSize() {
        return size;
    }

    /**
     * Get deletionlocked
     *
     * @return deletionlocked
     **/

    public Boolean getDeletionlocked() {
        return deletionlocked;
    }

    /**
     * Get directorypath
     *
     * @return directorypath
     **/

    public String getDirectorypath() {
        return directorypath;
    }

    /**
     * Get state
     *
     * @return state
     **/

    public StateEnum getState() {
        return state;
    }

    /**
     * The start time of the session capturing in ISO8601 format
     *
     * @return capturingstart
     **/
    @ApiModelProperty(example = "2016-05-11T11:35:31.170+02:00", value = "The start time of the session capturing in ISO8601 format")
    public Date getCapturingstart() {
        return capturingstart == null ? null : (Date) capturingstart.clone();
    }

    /**
     * Capturing duration in milliseconds
     *
     * @return capturingduration
     **/
    @ApiModelProperty(value = "Capturing duration in milliseconds")
    public Long getCapturingduration() {
        return capturingduration;
    }

    /**
     * Get version
     *
     * @return version
     **/

    public String getVersion() {
        return version;
    }

    public SessionMetadata labels(List<String> labels) {
        this.labels = labels;
        return this;
    }

    public SessionMetadata addLabelsItem(String labelsItem) {
        if (this.labels == null) {
            this.labels = new ArrayList<>();
        }
        this.labels.add(labelsItem);
        return this;
    }

    /**
     * Get labels
     *
     * @return labels
     **/

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    /**
     * Get recordingtype
     *
     * @return recordingtype
     **/

    public String getRecordingtype() {
        return recordingtype;
    }

    /**
     * Get agent
     *
     * @return agent
     **/

    public String getAgent() {
        return agent;
    }

    /**
     * Get numberofpurepaths
     *
     * @return numberofpurepaths
     **/

    public Integer getNumberofpurepaths() {
        return numberofpurepaths;
    }

    /**
     * Get continuoussession
     *
     * @return continuoussession
     **/

    public Boolean getContinuoussession() {
        return continuoussession;
    }

    @Override
    public String toString() {
        return "class SessionMetadata {\n" +
                "    id: " + PerfSigUIUtils.toIndentedString(super.getId()) + "\n" +
                "    storedsessiontype: " + PerfSigUIUtils.toIndentedString(storedsessiontype) + "\n" +
                "    sessiontype: " + PerfSigUIUtils.toIndentedString(sessiontype) + "\n" +
                "    systemprofile: " + PerfSigUIUtils.toIndentedString(systemprofile) + "\n" +
                "    href: " + PerfSigUIUtils.toIndentedString(super.getHref()) + "\n" +
                "    name: " + PerfSigUIUtils.toIndentedString(name) + "\n" +
                "    description: " + PerfSigUIUtils.toIndentedString(description) + "\n" +
                "    size: " + PerfSigUIUtils.toIndentedString(size) + "\n" +
                "    deletionlocked: " + PerfSigUIUtils.toIndentedString(deletionlocked) + "\n" +
                "    directorypath: " + PerfSigUIUtils.toIndentedString(directorypath) + "\n" +
                "    state: " + PerfSigUIUtils.toIndentedString(state) + "\n" +
                "    capturingstart: " + PerfSigUIUtils.toIndentedString(capturingstart) + "\n" +
                "    capturingduration: " + PerfSigUIUtils.toIndentedString(capturingduration) + "\n" +
                "    version: " + PerfSigUIUtils.toIndentedString(version) + "\n" +
                "    labels: " + PerfSigUIUtils.toIndentedString(labels) + "\n" +
                "    recordingtype: " + PerfSigUIUtils.toIndentedString(recordingtype) + "\n" +
                "    agent: " + PerfSigUIUtils.toIndentedString(agent) + "\n" +
                "    numberofpurepaths: " + PerfSigUIUtils.toIndentedString(numberofpurepaths) + "\n" +
                "    continuoussession: " + PerfSigUIUtils.toIndentedString(continuoussession) + "\n" +
                "}";
    }

    /**
     * Gets or Sets state
     */
    @JsonAdapter(StateEnum.Adapter.class)
    public enum StateEnum {
        INPROGRESS("inprogress"),

        FINISHED("finished"),

        CORRUPT("corrupt"),

        INCOMPLETE("incomplete");

        private final String value;

        StateEnum(String value) {
            this.value = value;
        }

        public static StateEnum fromValue(String text) {
            for (StateEnum b : StateEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static class Adapter extends TypeAdapter<StateEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final StateEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public StateEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return StateEnum.fromValue(String.valueOf(value));
            }
        }
    }
}
