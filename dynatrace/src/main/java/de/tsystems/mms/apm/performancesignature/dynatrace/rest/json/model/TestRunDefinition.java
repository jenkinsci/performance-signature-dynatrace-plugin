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
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import io.swagger.annotations.ApiModelProperty;

import static de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun.CategoryEnum;

/**
 * TestRunDefinition
 */

public class TestRunDefinition {
    @SerializedName("versionBuild")
    private String versionBuild;
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
    @SerializedName("category")
    private CategoryEnum category;

    public TestRunDefinition(int versionBuild, String performance) {
        this.versionBuild = String.valueOf(versionBuild);
        this.category = CategoryEnum.fromValue(performance);
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

    /**
     * Get versionMilestone
     *
     * @return versionMilestone
     **/
    public String getVersionMilestone() {
        return versionMilestone;
    }

    /**
     * Get versionMinor
     *
     * @return versionMinor
     **/
    public String getVersionMinor() {
        return versionMinor;
    }

    /**
     * Get versionRevision
     *
     * @return versionRevision
     **/
    public String getVersionRevision() {
        return versionRevision;
    }

    /**
     * Get marker
     *
     * @return marker
     **/
    public String getMarker() {
        return marker;
    }

    /**
     * Get platform
     *
     * @return platform
     **/
    public String getPlatform() {
        return platform;
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

    @Override
    public String toString() {
        return "class TestRunDefinition {\n" +
                "    versionBuild: " + PerfSigUIUtils.toIndentedString(versionBuild) + "\n" +
                "    versionMajor: " + PerfSigUIUtils.toIndentedString(versionMajor) + "\n" +
                "    versionMilestone: " + PerfSigUIUtils.toIndentedString(versionMilestone) + "\n" +
                "    versionMinor: " + PerfSigUIUtils.toIndentedString(versionMinor) + "\n" +
                "    versionRevision: " + PerfSigUIUtils.toIndentedString(versionRevision) + "\n" +
                "    marker: " + PerfSigUIUtils.toIndentedString(marker) + "\n" +
                "    platform: " + PerfSigUIUtils.toIndentedString(platform) + "\n" +
                "    category: " + PerfSigUIUtils.toIndentedString(category) + "\n" +
                "}";
    }
}
