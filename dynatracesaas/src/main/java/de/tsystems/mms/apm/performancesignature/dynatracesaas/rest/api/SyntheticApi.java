package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.api;

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.ExternalSyntheticEvents;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.ExternalSyntheticTests;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.StateModification;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SyntheticApi {
    /**
     * Push information about external Synthetic Events
     *
     * @param externalSyntheticEvents (required)
     * @return Call&lt;Void&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @POST("synthetic/ext/events")
    Call<Void> pushEvents(
            @Body ExternalSyntheticEvents externalSyntheticEvents
    );

    /**
     * Modify the operation state of all external monitors
     *
     * @param body (optional)
     * @return Call&lt;String&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @POST("synthetic/ext/stateModifications")
    Call<String> pushStateModification(
            @Body StateModification body
    );

    /**
     * Push Information about Synthetic Tests, Locations and Test Results.
     *
     * @param externalSyntheticTests Information about Synthetic Tests, Locations and Test Results. (required)
     * @return Call&lt;String&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @POST("synthetic/ext/tests")
    Call<String> testResults(
            @Body ExternalSyntheticTests externalSyntheticTests
    );

}
