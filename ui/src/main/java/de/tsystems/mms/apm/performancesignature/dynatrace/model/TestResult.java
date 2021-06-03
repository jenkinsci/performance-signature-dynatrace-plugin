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

package de.tsystems.mms.apm.performancesignature.dynatrace.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import io.swagger.annotations.ApiModelProperty;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * TestResult
 */

@ExportedBean
public class TestResult {
    @SerializedName("name")
    private String name;

    @SerializedName("status")
    private StatusEnum status;

    @SerializedName("exectime")
    private Date exectime;

    @SerializedName("package")
    private String packageName;

    @SerializedName("platform")
    private String platform;

    @SerializedName("measures")
    private List<TestMeasure> measures;

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
     * Get status
     *
     * @return status
     **/
    @Exported
    public StatusEnum getStatus() {
        return status;
    }

    /**
     * Start time of the test in ISO 8601 compatible date/time of format: yyyy-MM-dd&#39;T&#39;HH:mm:ss.SSSXXX
     *
     * @return exectime
     **/
    @Exported
    @ApiModelProperty(example = "2016-07-18T16:44:00.055+02:00", value = "Start time of the test in ISO 8601 compatible date/time of format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public Date getExectime() {
        return exectime == null ? null : (Date) exectime.clone();
    }

    /**
     * Get _package
     *
     * @return _package
     **/
    @Exported
    public String getPackage() {
        return packageName;
    }

    /**
     * Get platform
     *
     * @return platform
     **/
    @Exported
    public String getPlatform() {
        return platform;
    }

    public TestResult measures(List<TestMeasure> measures) {
        this.measures = measures;
        return this;
    }

    public TestResult addMeasuresItem(TestMeasure measuresItem) {
        if (this.measures == null) {
            this.measures = new ArrayList<>();
        }
        this.measures.add(measuresItem);
        return this;
    }

    public TestMeasure getMeasure(final String metricGroup, final String metric) {
        return measures.stream()
                .filter(measure -> measure.getMetricGroup().equals(metricGroup) && measure.getName().equals(metric))
                .findFirst().orElse(null);
    }

    /**
     * Get measures
     *
     * @return measures
     **/
    @Exported
    public List<TestMeasure> getMeasures() {
        return measures;
    }

    public void setMeasures(List<TestMeasure> measures) {
        this.measures = measures;
    }

    public String getStatusIcon() {
        switch (status) {
            case PASSED:
                return "check";
            case FAILED:
                return "times-circle";
            case IMPROVED:
                return "arrow-up";
            case DEGRADED:
                return "arrow-down";
            case VOLATILE:
                return "sort";
            default:
                return "";
        }
    }

    public String getStatusColor() {
        switch (status) {
            case PASSED:
                return "#2AB06F";
            case FAILED:
                return "#DC172A";
            case IMPROVED:
                return "#2AB6F4";
            case DEGRADED:
                return "#EF651F";
            case VOLATILE:
                return "#FFE11C";
            default:
                return "";
        }
    }

    @Override
    public String toString() {
        return "class TestResult {\n" +
                "    name: " + PerfSigUIUtils.toIndentedString(name) + "\n" +
                "    status: " + PerfSigUIUtils.toIndentedString(status) + "\n" +
                "    exectime: " + PerfSigUIUtils.toIndentedString(exectime) + "\n" +
                "    _package: " + PerfSigUIUtils.toIndentedString(packageName) + "\n" +
                "    platform: " + PerfSigUIUtils.toIndentedString(platform) + "\n" +
                "    measures: " + PerfSigUIUtils.toIndentedString(measures) + "\n" +
                "}";
    }
    /**
     * Gets or Sets category
     */
    @JsonAdapter(TestResult.StatusEnum.Adapter.class)
    public enum StatusEnum {
        FAILED("Failed"),
        VOLATILE("Volatile"),
        DEGRADED("Degraded"),
        IMPROVED("Improved"),
        PASSED("Passed"),
        NONE("None");

        private final String value;

        StatusEnum(String value) {
            this.value = value;
        }

        public static TestResult.StatusEnum fromValue(String text) {
            return Arrays.stream(StatusEnum.values()).filter(b -> b.value.equalsIgnoreCase(text)).findFirst().orElse(null);
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static class Adapter extends TypeAdapter<TestResult.StatusEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final TestResult.StatusEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public TestResult.StatusEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return TestResult.StatusEnum.fromValue(value);
            }
        }
    }
}
