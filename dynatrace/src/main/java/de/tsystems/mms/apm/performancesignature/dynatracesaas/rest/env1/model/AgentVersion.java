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

/*
 * Dynatrace Environment API
 * Documentation of the Dynatrace REST API. Refer to the [help page](https://www.dynatrace.com/support/help/shortlink/section-api) to read about use-cases and examples.
 *
 * OpenAPI spec version: 1.0
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env1.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import static de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils.toIndentedString;

/**
 * Defines the version of the agent currently running on the entity.
 */
@ApiModel(description = "Defines the version of the agent currently running on the entity.")

public class AgentVersion {
    @SerializedName("major")
    private Integer major;

    @SerializedName("minor")
    private Integer minor;

    @SerializedName("revision")
    private Integer revision;

    @SerializedName("sourceRevision")
    private String sourceRevision;

    @SerializedName("timestamp")
    private String timestamp;

    public AgentVersion major(Integer major) {
        this.major = major;
        return this;
    }

    /**
     * The major version number.
     *
     * @return major
     **/
    @ApiModelProperty(value = "The major version number.")
    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public AgentVersion minor(Integer minor) {
        this.minor = minor;
        return this;
    }

    /**
     * The minor version number.
     *
     * @return minor
     **/
    @ApiModelProperty(value = "The minor version number.")
    public Integer getMinor() {
        return minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }

    public AgentVersion revision(Integer revision) {
        this.revision = revision;
        return this;
    }

    /**
     * The revision number.
     *
     * @return revision
     **/
    @ApiModelProperty(value = "The revision number.")
    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public AgentVersion sourceRevision(String sourceRevision) {
        this.sourceRevision = sourceRevision;
        return this;
    }

    /**
     * A string representation of the SVN revision number.
     *
     * @return sourceRevision
     **/
    @ApiModelProperty(value = "A string representation of the SVN revision number.")
    public String getSourceRevision() {
        return sourceRevision;
    }

    public void setSourceRevision(String sourceRevision) {
        this.sourceRevision = sourceRevision;
    }

    public AgentVersion timestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * A timestamp string: format \&quot;yyyymmdd-hhmmss
     *
     * @return timestamp
     **/
    @ApiModelProperty(value = "A timestamp string: format \"yyyymmdd-hhmmss")
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "class AgentVersion {\n"
                + "    major: " + toIndentedString(major) + "\n"
                + "    minor: " + toIndentedString(minor) + "\n"
                + "    revision: " + toIndentedString(revision) + "\n"
                + "    sourceRevision: " + toIndentedString(sourceRevision) + "\n"
                + "    timestamp: " + toIndentedString(timestamp) + "\n"
                + "}";
    }
}

