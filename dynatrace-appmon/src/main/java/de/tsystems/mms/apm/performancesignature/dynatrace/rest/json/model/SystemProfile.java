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

/**
 * System Profile Metadata
 */
@ApiModel(description = "System Profile Metadata")

public class SystemProfile {
    @SerializedName("id")
    private String id;

    @SerializedName("description")
    private String description;

    @SerializedName("enabled")
    private Boolean enabled;

    @SerializedName("isrecording")
    private Boolean isrecording;

    /**
     * System Profile id
     *
     * @return id
     **/
    @ApiModelProperty(value = "System Profile id")
    public String getId() {
        return id;
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
     * Get enabled
     *
     * @return enabled
     **/

    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * Session recording state
     *
     * @return isrecording
     **/
    @ApiModelProperty(value = "Session recording state")
    public Boolean getIsrecording() {
        return isrecording;
    }

    @Override
    public String toString() {
        return "class SystemProfile {\n" +
                "    id: " + PerfSigUIUtils.toIndentedString(id) + "\n" +
                "    description: " + PerfSigUIUtils.toIndentedString(description) + "\n" +
                "    enabled: " + PerfSigUIUtils.toIndentedString(enabled) + "\n" +
                "    isrecording: " + PerfSigUIUtils.toIndentedString(isrecording) + "\n" +
                "}";
    }
}
