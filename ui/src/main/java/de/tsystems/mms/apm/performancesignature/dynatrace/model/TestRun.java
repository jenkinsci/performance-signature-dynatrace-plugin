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
import io.swagger.annotations.ApiModelProperty;
import org.kohsuke.stapler.export.Exported;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * TestRun
 */

public class TestRun extends BaseReference {
    @SerializedName("category")
    private CategoryEnum category;
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
    @SerializedName("platform")
    private String platform;
    @SerializedName("startTime")
    private Date startTime;
    @SerializedName("sessionId")
    private String sessionId;
    @SerializedName("session")
    private String session;
    @SerializedName("message")
    private String message;
    @SerializedName("finished")
    private boolean finished;
    @SerializedName("numDegraded")
    private int numDegraded;
    @SerializedName("numFailed")
    private int numFailed;
    @SerializedName("numImproved")
    private int numImproved;
    @SerializedName("numInvalidated")
    private int numInvalidated;
    @SerializedName("numPassed")
    private int numPassed;
    @SerializedName("numVolatile")
    private int numVolatile;
    @SerializedName("testResults")
    private List<TestResult> testResults;

    /**
     * Get category
     *
     * @return category
     **/
    @Exported
    @ApiModelProperty(example = "unit")
    public CategoryEnum getCategory() {
        return category;
    }

    /**
     * Get versionBuild
     *
     * @return versionBuild
     **/
    @Exported
    public String getVersionBuild() {
        return versionBuild;
    }

    /**
     * Get versionMajor
     *
     * @return versionMajor
     **/
    @Exported
    public String getVersionMajor() {
        return versionMajor;
    }

    /**
     * Get versionMilestone
     *
     * @return versionMilestone
     **/
    @Exported
    public String getVersionMilestone() {
        return versionMilestone;
    }

    /**
     * Get versionMinor
     *
     * @return versionMinor
     **/
    @Exported
    public String getVersionMinor() {
        return versionMinor;
    }

    /**
     * Get versionRevision
     *
     * @return versionRevision
     **/
    @Exported
    public String getVersionRevision() {
        return versionRevision;
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

    /**
     * Test run start time in ISO 8601 compatible date/time of format: yyyy-MM-dd&#39;T&#39;HH:mm:ss.SSSXXX
     *
     * @return startTime
     **/
    @Exported
    @ApiModelProperty(example = "2016-05-11T11:35:31.170+02:00", value = "Test run start time in ISO 8601 compatible date/time of format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public Date getStartTime() {
        return startTime == null ? null : (Date) startTime.clone();
    }

    /**
     * Get sessionId
     *
     * @return sessionId
     **/
    @Exported
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Get session
     *
     * @return session
     **/
    @Exported
    public String getSession() {
        return session;
    }

    /**
     * Get message
     *
     * @return message
     **/
    @Exported
    public String getMessage() {
        return message;
    }

    public static TestRun mergeTestRuns(final List<TestRun> testRuns) {
        TestRun newTestRun = new TestRun();
        if (testRuns != null && !testRuns.isEmpty()) {
            testRuns.forEach(otherTestRun -> {
                newTestRun.numDegraded += otherTestRun.numDegraded;
                newTestRun.numFailed += otherTestRun.numFailed;
                newTestRun.numImproved += otherTestRun.numImproved;
                newTestRun.numInvalidated += otherTestRun.numInvalidated;
                newTestRun.numPassed += otherTestRun.numPassed;
                newTestRun.numVolatile += otherTestRun.numVolatile;
                newTestRun.getTestResults().addAll(otherTestRun.getTestResults());
            });
        }
        return newTestRun;
    }

    /**
     * Get numDegraded
     *
     * @return numDegraded
     **/
    @Exported
    public int getNumDegraded() {
        return numDegraded;
    }

    /**
     * Get numFailed
     *
     * @return numFailed
     **/
    @Exported
    public int getNumFailed() {
        return numFailed;
    }

    /**
     * Get numImproved
     *
     * @return numImproved
     **/
    @Exported
    public int getNumImproved() {
        return numImproved;
    }

    /**
     * Get numInvalidated
     *
     * @return numInvalidated
     **/
    @Exported
    public int getNumInvalidated() {
        return numInvalidated;
    }

    /**
     * Get numPassed
     *
     * @return numPassed
     **/
    @Exported
    public int getNumPassed() {
        return numPassed;
    }

    /**
     * Get numVolatile
     *
     * @return numVolatile
     **/
    @Exported
    public int getNumVolatile() {
        return numVolatile;
    }

    /**
     * Get finished
     *
     * @return finished
     **/
    @Exported
    public boolean getFinished() {
        return finished;
    }

    @Override
    public String toString() {
        return "class TestRun {\n" +
                "    id: " + PerfSigUIUtils.toIndentedString(super.getId()) + "\n" +
                "    category: " + PerfSigUIUtils.toIndentedString(category) + "\n" +
                "    versionBuild: " + PerfSigUIUtils.toIndentedString(versionBuild) + "\n" +
                "    versionMajor: " + PerfSigUIUtils.toIndentedString(versionMajor) + "\n" +
                "    versionMilestone: " + PerfSigUIUtils.toIndentedString(versionMilestone) + "\n" +
                "    versionMinor: " + PerfSigUIUtils.toIndentedString(versionMinor) + "\n" +
                "    versionRevision: " + PerfSigUIUtils.toIndentedString(versionRevision) + "\n" +
                "    platform: " + PerfSigUIUtils.toIndentedString(platform) + "\n" +
                "    startTime: " + PerfSigUIUtils.toIndentedString(startTime) + "\n" +
                "    sessionId: " + PerfSigUIUtils.toIndentedString(sessionId) + "\n" +
                "    session: " + PerfSigUIUtils.toIndentedString(session) + "\n" +
                "    message: " + PerfSigUIUtils.toIndentedString(message) + "\n" +
                "    href: " + PerfSigUIUtils.toIndentedString(super.getHref()) + "\n" +
                "    numDegraded: " + PerfSigUIUtils.toIndentedString(numDegraded) + "\n" +
                "    numFailed: " + PerfSigUIUtils.toIndentedString(numFailed) + "\n" +
                "    numImproved: " + PerfSigUIUtils.toIndentedString(numImproved) + "\n" +
                "    numInvalidated: " + PerfSigUIUtils.toIndentedString(numInvalidated) + "\n" +
                "    numPassed: " + PerfSigUIUtils.toIndentedString(numPassed) + "\n" +
                "    numVolatile: " + PerfSigUIUtils.toIndentedString(numVolatile) + "\n" +
                "    finished: " + PerfSigUIUtils.toIndentedString(finished) + "\n" +
                "    testResults: " + PerfSigUIUtils.toIndentedString(testResults) + "\n" +
                "}";
    }

    /**
     * Get testResults
     *
     * @return testResults
     **/
    @Exported
    public List<TestResult> getTestResults() {
        if (testResults == null) {
            testResults = new ArrayList<>();
        }
        return testResults;
    }

    /**
     * Gets or Sets category
     */
    @JsonAdapter(CategoryEnum.Adapter.class)
    public enum CategoryEnum {
        UNIT("unit"),
        UIDRIVEN("uidriven"),
        PERFORMANCE("performance"),
        WEBAPI("webapi"),
        EXTERNAL("external");

        private final String value;

        CategoryEnum(String value) {
            this.value = value;
        }

        public static CategoryEnum fromValue(String text) {
            return Arrays.stream(CategoryEnum.values()).filter(b -> b.value.equalsIgnoreCase(text)).findFirst().orElse(null);
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static class Adapter extends TypeAdapter<CategoryEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final CategoryEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public CategoryEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return CategoryEnum.fromValue(value);
            }
        }
    }
}
