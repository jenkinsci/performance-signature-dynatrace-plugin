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

import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.ActivationStatus;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SystemProfile;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SystemProfileConfigurations;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SystemProfiles;
import retrofit2.Call;
import retrofit2.http.*;

import static de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.ApiClient.API_SUFFIX;

public interface SystemProfilesApi {
    /**
     * List System Profiles
     * Get a list of all System Profiles of the AppMon Server.
     *
     * @return Call&lt;SystemProfiles&gt;
     */
    @GET(API_SUFFIX + "profiles")
    Call<SystemProfiles> getProfiles();


    /**
     * Activation status of System Profile configuration
     * Retrieve the activation state of a System Profile configuration.
     *
     * @param profileid  System Profile id (required)
     * @param configname Configuration name (required)
     * @return Call&lt;ActivationStatus&gt;
     */
    @GET(API_SUFFIX + "profiles/{profileid}/configurations/{configname}/status")
    Call<ActivationStatus> getSystemProfileConfigurationStatus(
            @Path("profileid") String profileid, @Path("configname") String configname
    );

    /**
     * List System Profile configurations
     * Get a list of all configurations of the specified System Profile.
     *
     * @param profileid System Profile id (required)
     * @return Call&lt;SystemProfileConfigurations&gt;
     */
    @GET(API_SUFFIX + "profiles/{profileid}/configurations")
    Call<SystemProfileConfigurations> getSystemProfileConfigurations(
            @Path("profileid") String profileid
    );

    /**
     * System Profile Metadata
     * Get a JSON representation describing the System Profile and its meta data.
     *
     * @param profileid System Profile id (required)
     * @return Call&lt;SystemProfile&gt;
     */
    @GET(API_SUFFIX + "profiles/{profileid}")
    Call<SystemProfile> getSystemProfileMetaData(
            @Path("profileid") String profileid
    );

    /**
     * Activation status of System Profile
     * Retrieve the activation state of a System Profile.
     *
     * @param profileid System Profile id (required)
     * @return Call&lt;ActivationStatus&gt;
     */
    @GET(API_SUFFIX + "profiles/{profileid}/status")
    Call<ActivationStatus> getSystemProfileState(
            @Path("profileid") String profileid
    );

    /**
     * Activate System Profile configuration
     * Change the activation state of a System Profile. Activating a configuration automatically sets all other configurations to DISABLED. Manually setting the activation state to DISABLED via this call is not allowed.
     *
     * @param profileid  System Profile id (required)
     * @param configname Configuration name (required)
     * @param body       Activation state (optional)
     * @return Call&lt;Void&gt;
     */
    @Headers({
            "Content-Type:application/json"
    })
    @PUT(API_SUFFIX + "profiles/{profileid}/configurations/{configname}/status")
    Call<Void> putSystemProfileConfigurationStatus(
            @Path("profileid") String profileid, @Path("configname") String configname, @Body ActivationStatus body
    );

    /**
     * Enable/disable System Profile
     * Change the activation state of a System Profile.
     *
     * @param profileid System Profile id (required)
     * @param body      Activation state (optional)
     * @return Call&lt;Void&gt;
     */
    @Headers({
            "Content-Type:application/json"
    })
    @PUT(API_SUFFIX + "profiles/{profileid}/status")
    Call<Void> putSystemProfileState(
            @Path("profileid") String profileid, @Body ActivationStatus body
    );

}
