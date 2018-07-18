package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.api;

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.ClusterVersion;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ClusterVersionApi {
    /**
     * Gets the current version of the cluster server.
     *
     * @return Call&lt;ClusterVersion&gt;
     */
    @GET("config/clusterversion")
    Call<ClusterVersion> getVersion();


}
