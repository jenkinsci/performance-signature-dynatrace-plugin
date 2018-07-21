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

package de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.api;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.ExternalTest;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.TestRunDefinition;
import retrofit2.Call;
import retrofit2.http.*;

import static de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.ApiClient.API_SUFFIX;

public interface TestAutomationApi {
    /**
     * Mark test run as finished
     * Returns all test run details (including test executions) of a finished test run.
     *
     * @param profileid System profile id (required)
     * @param testrunid UUID of the test run (required)
     * @return Call&lt;TestRun&gt;
     */
    @POST(API_SUFFIX + "profiles/{profileid}/testruns/{testrunid}/finish")
    Call<TestRun> finishTestRun(
            @Path("profileid") String profileid, @Path("testrunid") String testrunid
    );

    /**
     * Get test run by id
     * Get all test run details including test executions.
     *
     * @param profileid System profile id (required)
     * @param testrunid UUID of the test run (required)
     * @return Call&lt;TestRun&gt;
     */
    @GET(API_SUFFIX + "profiles/{profileid}/testruns/{testrunid}")
    Call<TestRun> getTestrunById(
            @Path("profileid") String profileid, @Path("testrunid") String testrunid
    );

    /**
     * Set test execution&#39;s state to FAILED
     * Test execution is located using testRunId (UUID) and full test name (including package and class). Returns all details of a test run which is bound with marked test execution.
     *
     * @param profileid System profile id (required)
     * @param testrunid UUID of the test run (required)
     * @param testId    Full test name including package description and class name (if applicable) (required)
     * @return Call&lt;TestRun&gt;
     */
    @POST(API_SUFFIX + "profiles/{profileid}/testruns/{testrunid}/markAsFailed")
    Call<TestRun> markTestExecutionAsFailed(
            @Path("profileid") String profileid, @Path("testrunid") String testrunid, @Query("testId") String testId
    );

    /**
     * Post test result
     * Post a test result for a registered test run of category &#39;external&#39;.
     *
     * @param profileid System profile id (required)
     * @param testrunid UUID of the test run (required)
     * @param body      Test result (required)
     * @return Call&lt;Void&gt;
     */
    @Headers({
            "Content-Type:application/json"
    })
    @POST(API_SUFFIX + "profiles/{profileid}/testruns/{testrunid}")
    Call<Void> postExternalTestData(
            @Path("profileid") String profileid, @Path("testrunid") String testrunid, @Body ExternalTest body
    );

    /**
     * Register test run
     * Register a test run with provided parameters. The reply contains all test run&#39;s details including the UUID.  - **category** is limited to one of the following values: &#39;unit&#39; (default), &#39;uidriven&#39;, &#39;performance&#39;, &#39;webapi&#39; or &#39;external&#39;. - **marker** is a label used in the Test Automation dashlet charts. - if the **platform** is left blank, the agent will detect it automatically. - if the **includedMetrics** is provided, only test results for these metrics will be collected in this test run. In order to collect all test results for a given metric group you can pass just the group name or leave the metric name blank. - **additionalMetaData** can be used to provide additional data stored in Test Run, like Jenkins build ID, list of commiters, etc. Example Value:  &#x60;&#x60;&#x60; \&quot;additionalMetaData\&quot;: [{  \&quot;key1\&quot;: \&quot;value1\&quot;,  \&quot;key2\&quot;: \&quot;value2\&quot; }] &#x60;&#x60;&#x60;
     *
     * @param profileid System profile id (required)
     * @param body      Test run parameters (required)
     * @return Call&lt;TestRun&gt;
     */
    @Headers({
            "Content-Type:application/json"
    })
    @POST(API_SUFFIX + "profiles/{profileid}/testruns")
    Call<TestRun> postTestRun(
            @Path("profileid") String profileid, @Body TestRunDefinition body
    );

}
