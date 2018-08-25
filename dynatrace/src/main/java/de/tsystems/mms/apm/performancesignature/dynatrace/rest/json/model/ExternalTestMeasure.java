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
import io.swagger.annotations.ApiModelProperty;

import static de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils.toIndentedString;

/**
 * ExternalTestMeasure
 */

public class ExternalTestMeasure {
    @SerializedName("name")
    private String name = null;
    @SerializedName("value")
    private Double value = null;
    @SerializedName("timestamp")
    private String timestamp = null;
    @SerializedName("unit")
    private String unit = null;
    @SerializedName("minValue")
    private Double minValue = null;
    @SerializedName("maxValue")
    private Double maxValue = null;
    @SerializedName("color")
    private String color = null;

    /**
     * Get name
     *
     * @return name
     **/
    @ApiModelProperty(required = true)
    public String getName() {
        return name;
    }

    /**
     * Get value
     *
     * @return value
     **/
    @ApiModelProperty(required = true)
    public Double getValue() {
        return value;
    }

    /**
     * Timestamp in ISO 8601 compatible date/time of format: yyyy-MM-dd&#39;T&#39;HH:mm:ss.SSSXXX
     *
     * @return timestamp
     **/
    @ApiModelProperty(example = "2016-05-11T11:35:31.170+02:00", value = "Timestamp in ISO 8601 compatible date/time of format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Get unit
     *
     * @return unit
     **/
    @ApiModelProperty()
    public String getUnit() {
        return unit;
    }

    /**
     * Get minValue
     *
     * @return minValue
     **/
    @ApiModelProperty()
    public Double getMinValue() {
        return minValue;
    }

    /**
     * Get maxValue
     *
     * @return maxValue
     **/
    @ApiModelProperty()
    public Double getMaxValue() {
        return maxValue;
    }

    /**
     * Get color
     *
     * @return color
     **/
    @ApiModelProperty(example = "#FF0000")
    public String getColor() {
        return color;
    }

    @Override
    public String toString() {

        return "class ExternalTestMeasure {\n" +
                "    name: " + toIndentedString(name) + "\n" +
                "    value: " + toIndentedString(value) + "\n" +
                "    timestamp: " + toIndentedString(timestamp) + "\n" +
                "    unit: " + toIndentedString(unit) + "\n" +
                "    minValue: " + toIndentedString(minValue) + "\n" +
                "    maxValue: " + toIndentedString(maxValue) + "\n" +
                "    color: " + toIndentedString(color) + "\n" +
                "}";
    }
}

