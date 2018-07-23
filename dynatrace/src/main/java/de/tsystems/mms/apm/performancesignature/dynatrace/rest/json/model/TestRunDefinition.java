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

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;

import static de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun.CategoryEnum;
import static de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils.toIndentedString;

/**
 * TestRunDefinition
 */

public class TestRunDefinition {
    @SerializedName("versionBuild")
    private final String versionBuild;
    @SerializedName("category")
    private CategoryEnum category;
    @SerializedName("versionMajor")
    private String versionMajor;
    @SerializedName("versionMilestone")
    private String versionMilestone;
    @SerializedName("versionMinor")
    private String versionMinor;
    @SerializedName("versionRevision")
    private String versionRevision;
    @SerializedName("marker")
    private String marker;
    @SerializedName("platform")
    private String platform;
    @SerializedName("additionalMetaData")
    private Map<String, String> additionalMetaData;

    public TestRunDefinition(final int versionBuild) {
        this.versionBuild = String.valueOf(versionBuild);
    }

    public Map<String, String> getAdditionalMetaData() {
        return additionalMetaData;
    }

    public TestRunDefinition addAdditionalMetaData(final String key, final String value) {
        if (this.additionalMetaData == null) {
            this.additionalMetaData = new HashMap<>();
        }
        this.additionalMetaData.put(key, value);
        return this;
    }

    /**
     * Get versionBuild
     *
     * @return versionBuild
     **/

    public String getVersionBuild() {
        return versionBuild;
    }

    /**
     * Get versionMajor
     *
     * @return versionMajor
     **/

    public String getVersionMajor() {
        return versionMajor;
    }

    public TestRunDefinition setVersionMajor(final String versionMajor) {
        this.versionMajor = versionMajor;
        return this;
    }

    /**
     * Get versionMilestone
     *
     * @return versionMilestone
     **/
    public String getVersionMilestone() {
        return versionMilestone;
    }

    public TestRunDefinition setVersionMilestone(final String versionMilestone) {
        this.versionMilestone = versionMilestone;
        return this;
    }

    /**
     * Get versionMinor
     *
     * @return versionMinor
     **/
    public String getVersionMinor() {
        return versionMinor;
    }

    public TestRunDefinition setVersionMinor(final String versionMinor) {
        this.versionMinor = versionMinor;
        return this;
    }

    /**
     * Get versionRevision
     *
     * @return versionRevision
     **/
    public String getVersionRevision() {
        return versionRevision;
    }

    public TestRunDefinition setVersionRevision(final String versionRevision) {
        this.versionRevision = versionRevision;
        return this;
    }

    /**
     * Get marker
     *
     * @return marker
     **/
    public String getMarker() {
        return marker;
    }

    public TestRunDefinition setMarker(final String marker) {
        this.marker = marker;
        return this;
    }

    /**
     * Get platform
     *
     * @return platform
     **/
    public String getPlatform() {
        return platform;
    }

    public TestRunDefinition setPlatform(final String platform) {
        this.platform = platform;
        return this;
    }

    /**
     * Get category
     *
     * @return category
     **/
    @ApiModelProperty(example = "unit")
    public CategoryEnum getCategory() {
        return category;
    }

    public TestRunDefinition setCategory(String category) {
        this.category = CategoryEnum.fromValue(category);
        return this;
    }

    @Override
    public String toString() {
        return "class TestRunDefinition {\n" +
                "    category: " + toIndentedString(category) + "\n" +
                "    versionMinor: " + toIndentedString(versionMinor) + "\n" +
                "    versionMajor: " + toIndentedString(versionMajor) + "\n" +
                "    versionBuild: " + toIndentedString(versionBuild) + "\n" +
                "    versionMilestone: " + toIndentedString(versionMilestone) + "\n" +
                "    versionRevision: " + toIndentedString(versionRevision) + "\n" +
                "    additionalMetaData: " + toIndentedString(additionalMetaData) + "\n" +
                "    marker: " + toIndentedString(marker) + "\n" +
                "    platform: " + toIndentedString(platform) + "\n" +
                "}";
    }
}
