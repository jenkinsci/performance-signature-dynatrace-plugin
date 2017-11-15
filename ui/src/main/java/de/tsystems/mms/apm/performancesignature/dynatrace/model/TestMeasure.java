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
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * TestMeasure
 */

@ExportedBean
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "testmeasure")
public class TestMeasure {
    @SerializedName("name")
    @XmlElement
    private String name;
    @SerializedName("metricGroup")
    @XmlElement
    private String metricGroup;
    @SerializedName("value")
    @XmlElement
    private Double value;
    @SerializedName("unit")
    @XmlElement
    private String unit;
    @SerializedName("expectedMin")
    @XmlElement
    private Double expectedMin;
    @SerializedName("expectedMax")
    @XmlElement
    private Double expectedMax;

    /**
     * Get name
     *
     * @return name
     **/
    @Exported
    public String getName() {
        return name;
    }

    /**
     * Get metricGroup
     *
     * @return metricGroup
     **/
    @Exported
    public String getMetricGroup() {
        return metricGroup;
    }

    /**
     * Get value
     *
     * @return value
     **/
    @Exported
    public Double getValue() {
        return value;
    }

    /**
     * Get unit
     *
     * @return unit
     **/
    @Exported
    public String getUnit() {
        return unit;
    }

    /**
     * Get expectedMin
     *
     * @return expectedMin
     **/
    @Exported
    public Double getExpectedMin() {
        return expectedMin;
    }

    /**
     * Get expectedMax
     *
     * @return expectedMax
     **/
    @Exported
    public Double getExpectedMax() {
        return expectedMax;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TestMeasure {\n");

        sb.append("    name: ").append(PerfSigUIUtils.toIndentedString(name)).append("\n");
        sb.append("    metricGroup: ").append(PerfSigUIUtils.toIndentedString(metricGroup)).append("\n");
        sb.append("    value: ").append(PerfSigUIUtils.toIndentedString(value)).append("\n");
        sb.append("    unit: ").append(PerfSigUIUtils.toIndentedString(unit)).append("\n");
        sb.append("    expectedMin: ").append(PerfSigUIUtils.toIndentedString(expectedMin)).append("\n");
        sb.append("    expectedMax: ").append(PerfSigUIUtils.toIndentedString(expectedMax)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
