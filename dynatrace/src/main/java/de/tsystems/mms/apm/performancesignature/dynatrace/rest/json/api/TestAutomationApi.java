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

import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Call;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.*;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.TestRunDefinition;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.PerfSigUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestAutomationApi {
    private ApiClient apiClient;

    public TestAutomationApi() {
        this(Configuration.getDefaultApiClient());
    }

    public TestAutomationApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Build call for finishTestRun
     *
     * @param profileid System profile id (required)
     * @param testrunid UUID of the test run (required)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call finishTestRunCall(String profileid, String testrunid) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/profiles/{profileid}/testruns/{testrunid}/finish"
                .replaceAll("\\{profileid\\}", PerfSigUtils.escapeString(profileid))
                .replaceAll("\\{testrunid\\}", PerfSigUtils.escapeString(testrunid));

        List<Pair> localVarQueryParams = new ArrayList<>();

        Map<String, String> localVarHeaderParams = new HashMap<>();
        Map<String, Object> localVarFormParams = new HashMap<>();
        localVarHeaderParams.put("Accept", "application/json");

        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams, null, localVarHeaderParams, localVarFormParams);
    }

    @SuppressWarnings("rawtypes")
    private Call finishTestRunValidateBeforeCall(String profileid, String testrunid) throws ApiException {
        // verify the required parameter 'profileid' is set
        if (profileid == null) {
            throw new ApiException("Missing the required parameter 'profileid' when calling finishTestRun");
        }

        // verify the required parameter 'testrunid' is set
        if (testrunid == null) {
            throw new ApiException("Missing the required parameter 'testrunid' when calling finishTestRun");
        }

        return finishTestRunCall(profileid, testrunid);
    }

    /**
     * Mark test run as finished
     * Returns all test run details (including test executions) of a finished test run.
     *
     * @param profileid System profile id (required)
     * @param testrunid UUID of the test run (required)
     * @return TestRun
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public TestRun finishTestRun(String profileid, String testrunid) throws ApiException {
        ApiResponse<TestRun> resp = finishTestRunWithHttpInfo(profileid, testrunid);
        return resp.getData();
    }

    /**
     * Mark test run as finished
     * Returns all test run details (including test executions) of a finished test run.
     *
     * @param profileid System profile id (required)
     * @param testrunid UUID of the test run (required)
     * @return ApiResponse&lt;TestRun&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<TestRun> finishTestRunWithHttpInfo(String profileid, String testrunid) throws ApiException {
        Call call = finishTestRunValidateBeforeCall(profileid, testrunid);
        Type localVarReturnType = new TypeToken<TestRun>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Build call for getTestrunById
     *
     * @param profileid System profile id (required)
     * @param testrunid UUID of the test run (required)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getTestrunByIdCall(String profileid, String testrunid) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/profiles/{profileid}/testruns/{testrunid}"
                .replaceAll("\\{profileid\\}", PerfSigUtils.escapeString(profileid))
                .replaceAll("\\{testrunid\\}", PerfSigUtils.escapeString(testrunid));

        List<Pair> localVarQueryParams = new ArrayList<>();

        Map<String, String> localVarHeaderParams = new HashMap<>();
        localVarHeaderParams.put("Accept", "application/json");

        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, null, localVarHeaderParams, null);
    }

    @SuppressWarnings("rawtypes")
    private Call getTestrunByIdValidateBeforeCall(String profileid, String testrunid) throws ApiException {
        // verify the required parameter 'profileid' is set
        if (profileid == null) {
            throw new ApiException("Missing the required parameter 'profileid' when calling getTestrunById");
        }

        // verify the required parameter 'testrunid' is set
        if (testrunid == null) {
            throw new ApiException("Missing the required parameter 'testrunid' when calling getTestrunById");
        }

        return getTestrunByIdCall(profileid, testrunid);
    }

    /**
     * Get test run by id
     * Get all test run details including test executions.
     *
     * @param profileid System profile id (required)
     * @param testrunid UUID of the test run (required)
     * @return TestRun
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public TestRun getTestrunById(String profileid, String testrunid) throws ApiException {
        ApiResponse<TestRun> resp = getTestrunByIdWithHttpInfo(profileid, testrunid);
        return resp.getData();
    }

    /**
     * Get test run by id
     * Get all test run details including test executions.
     *
     * @param profileid System profile id (required)
     * @param testrunid UUID of the test run (required)
     * @return ApiResponse&lt;TestRun&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<TestRun> getTestrunByIdWithHttpInfo(String profileid, String testrunid) throws ApiException {
        Call call = getTestrunByIdValidateBeforeCall(profileid, testrunid);
        Type localVarReturnType = new TypeToken<TestRun>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Build call for markTestExecutionAsFailed
     *
     * @param profileid System profile id (required)
     * @param testrunid UUID of the test run (required)
     * @param testId    Full test name including package description and class name (if applicable) (required)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call markTestExecutionAsFailedCall(String profileid, String testrunid, String testId) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/profiles/{profileid}/testruns/{testrunid}/markAsFailed"
                .replaceAll("\\{profileid\\}", PerfSigUtils.escapeString(profileid))
                .replaceAll("\\{testrunid\\}", PerfSigUtils.escapeString(testrunid));

        List<Pair> localVarQueryParams = new ArrayList<>();
        if (testId != null) {
            localVarQueryParams.addAll(apiClient.parameterToPair("testId", testId));
        }

        Map<String, String> localVarHeaderParams = new HashMap<>();
        Map<String, Object> localVarFormParams = new HashMap<>();
        localVarHeaderParams.put("Accept", "application/json");

        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams, null, localVarHeaderParams, localVarFormParams);
    }

    @SuppressWarnings("rawtypes")
    private Call markTestExecutionAsFailedValidateBeforeCall(String profileid, String testrunid, String testId) throws ApiException {
        // verify the required parameter 'profileid' is set
        if (profileid == null) {
            throw new ApiException("Missing the required parameter 'profileid' when calling markTestExecutionAsFailed");
        }

        // verify the required parameter 'testrunid' is set
        if (testrunid == null) {
            throw new ApiException("Missing the required parameter 'testrunid' when calling markTestExecutionAsFailed");
        }

        // verify the required parameter 'testId' is set
        if (testId == null) {
            throw new ApiException("Missing the required parameter 'testId' when calling markTestExecutionAsFailed");
        }

        return markTestExecutionAsFailedCall(profileid, testrunid, testId);
    }

    /**
     * Set test execution&#39;s state to FAILED
     * Test execution is located using testRunId (UUID) and full test name (including package and class). Returns all details of a test run which is bound with marked test execution.
     *
     * @param profileid System profile id (required)
     * @param testrunid UUID of the test run (required)
     * @param testId    Full test name including package description and class name (if applicable) (required)
     * @return TestRun
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public TestRun markTestExecutionAsFailed(String profileid, String testrunid, String testId) throws ApiException {
        ApiResponse<TestRun> resp = markTestExecutionAsFailedWithHttpInfo(profileid, testrunid, testId);
        return resp.getData();
    }

    /**
     * Set test execution&#39;s state to FAILED
     * Test execution is located using testRunId (UUID) and full test name (including package and class). Returns all details of a test run which is bound with marked test execution.
     *
     * @param profileid System profile id (required)
     * @param testrunid UUID of the test run (required)
     * @param testId    Full test name including package description and class name (if applicable) (required)
     * @return ApiResponse&lt;TestRun&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<TestRun> markTestExecutionAsFailedWithHttpInfo(String profileid, String testrunid, String testId) throws ApiException {
        Call call = markTestExecutionAsFailedValidateBeforeCall(profileid, testrunid, testId);
        Type localVarReturnType = new TypeToken<TestRun>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Build call for postTestRun
     *
     * @param profileid System profile id (required)
     * @param body      Test run parameters (required)
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call postTestRunCall(String profileid, TestRunDefinition body) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/profiles/{profileid}/testruns"
                .replaceAll("\\{profileid\\}", PerfSigUtils.escapeString(profileid));

        List<Pair> localVarQueryParams = new ArrayList<>();

        Map<String, String> localVarHeaderParams = new HashMap<>();
        Map<String, Object> localVarFormParams = new HashMap<>();
        localVarHeaderParams.put("Accept", "application/json");
        localVarHeaderParams.put("Content-Type", "application/json");

        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams, body, localVarHeaderParams, localVarFormParams);
    }

    @SuppressWarnings("rawtypes")
    private Call postTestRunValidateBeforeCall(String profileid, TestRunDefinition body) throws ApiException {
        // verify the required parameter 'profileid' is set
        if (profileid == null) {
            throw new ApiException("Missing the required parameter 'profileid' when calling postTestRun");
        }

        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException("Missing the required parameter 'body' when calling postTestRun");
        }

        return postTestRunCall(profileid, body);
    }

    /**
     * Register test run
     * Register a test run with provided parameters. The reply contains all test run&#39;s details including the UUID.  - **category** is limited to one of the following values: &#39;unit&#39; (default), &#39;uidriven&#39;, &#39;performance&#39;, &#39;webapi&#39; or &#39;external&#39;. - **marker** is a label used in the Test Automation dashlet charts. - if the **platform** is left blank, the agent will detect it automatically. - if the **includedMetrics** is provided, only test results for these metrics will be collected in this test run. In order to collect all test results for a given metric group you can pass just the group name or leave the metric name blank. - **additionalMetaData** can be used to provide additional data stored in Test Run, like Jenkins build ID, list of commiters, etc. Example Value:  &#x60;&#x60;&#x60; \&quot;additionalMetaData\&quot;: [{  \&quot;key1\&quot;: \&quot;value1\&quot;,  \&quot;key2\&quot;: \&quot;value2\&quot; }] &#x60;&#x60;&#x60;
     *
     * @param profileid System profile id (required)
     * @param body      Test run parameters (required)
     * @return TestRun
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public TestRun postTestRun(String profileid, TestRunDefinition body) throws ApiException {
        ApiResponse<TestRun> resp = postTestRunWithHttpInfo(profileid, body);
        return resp.getData();
    }

    /**
     * Register test run
     * Register a test run with provided parameters. The reply contains all test run&#39;s details including the UUID.  - **category** is limited to one of the following values: &#39;unit&#39; (default), &#39;uidriven&#39;, &#39;performance&#39;, &#39;webapi&#39; or &#39;external&#39;. - **marker** is a label used in the Test Automation dashlet charts. - if the **platform** is left blank, the agent will detect it automatically. - if the **includedMetrics** is provided, only test results for these metrics will be collected in this test run. In order to collect all test results for a given metric group you can pass just the group name or leave the metric name blank. - **additionalMetaData** can be used to provide additional data stored in Test Run, like Jenkins build ID, list of commiters, etc. Example Value:  &#x60;&#x60;&#x60; \&quot;additionalMetaData\&quot;: [{  \&quot;key1\&quot;: \&quot;value1\&quot;,  \&quot;key2\&quot;: \&quot;value2\&quot; }] &#x60;&#x60;&#x60;
     *
     * @param profileid System profile id (required)
     * @param body      Test run parameters (required)
     * @return ApiResponse&lt;TestRun&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<TestRun> postTestRunWithHttpInfo(String profileid, TestRunDefinition body) throws ApiException {
        Call call = postTestRunValidateBeforeCall(profileid, body);
        Type localVarReturnType = new TypeToken<TestRun>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }
}
