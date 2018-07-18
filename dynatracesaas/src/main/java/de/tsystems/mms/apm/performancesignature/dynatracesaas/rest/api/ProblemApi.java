package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.api;

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ProblemApi {
    /**
     * Deletes an existing comment of the specified problem.
     *
     * @param problemId The ID of the problem where you want to delete the comment. (required)
     * @param commentId The ID of the comment to be deleted. (required)
     * @return Call&lt;Void&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @DELETE("problem/details/{problemId}/comments/{commentId}")
    Call<Void> deleteComment(
            @Path("problemId") Long problemId, @Path("commentId") Long commentId
    );

    /**
     * Gets all the comments for the specified problem.
     *
     * @param problemId The ID of the problem where you want to read the comments. (required)
     * @return Call&lt;ProblemCommentList&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("problem/details/{problemId}/comments")
    Call<ProblemCommentList> getComment(
            @Path("problemId") Long problemId
    );

    /**
     * Gets the details about the specified problem.
     *
     * @param problemId The ID of the problem you&#39;re inquiring. (required)
     * @return Call&lt;ProblemDetailsResultWrapper&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("problem/details/{problemId}")
    Call<ProblemDetailsResultWrapper> getDetails(
            @Path("problemId") Long problemId
    );

    /**
     * Gets the information about problems within the specified timeframe.
     * Lists problems observed within a relative time along with problem&#39;s properties, plus the count of monitored entities for each impact level.
     *
     * @param relativeTime   Relative timeframe, back from the current time. (optional)
     * @param startTimestamp Start timestamp of the requested timeframe, in milliseconds (UTC). The start time must be earlier than the end time. (optional)
     * @param endTimestamp   End timestamp of the requested timeframe, in milliseconds (UTC). End time must be later than the start time.   If &#x60;endTimestamp&#x60; is later than the current time, Dynatrace automatically uses current time instead. (optional)
     * @param status         Filters the result problems by the status. (optional)
     * @param impactLevel    Filters the result problems by the impact level. (optional)
     * @param severityLevel  Filters the result problems by the severity level. (optional)
     * @param tag            Filters the result problems by the tags of affected entities. (optional)
     * @param expandDetails  Chooses to have rankedEvents exposed in a problem feed (optional)
     * @return Call&lt;ProblemFeedResultWrapper&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("problem/feed")
    Call<ProblemFeedResultWrapper> getFeed(
            @Query("relativeTime") String relativeTime, @Query("startTimestamp") Long startTimestamp, @Query("endTimestamp") Long endTimestamp, @Query("status") String status, @Query("impactLevel") String impactLevel, @Query("severityLevel") String severityLevel, @Query("tag") List<String> tag, @Query("expandDetails") Boolean expandDetails
    );

    /**
     * Gets the number of current problems.
     * Lists the number of open problems, split by impact level.
     *
     * @return Call&lt;ProblemStatusResultWrapper&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("problem/status")
    Call<ProblemStatusResultWrapper> getProblemStatus();


    /**
     * Adds a new comment to the specified problem.
     *
     * @param problemId The ID of the problem where you want to add the comment. (required)
     * @param body      JSON body of the request, containing the comment information. (optional)
     * @return Call&lt;ProblemComment&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @POST("problem/details/{problemId}/comments")
    Call<ProblemComment> pushComment(
            @Path("problemId") Long problemId, @retrofit2.http.Body ProblemComment body
    );

    /**
     * Updates an existing comment for the specified problem.
     *
     * @param problemId The ID of the problem where you want to edit the comment. (required)
     * @param commentId The ID of the comment you want to edit. (required)
     * @param body      JSON body of the request, containing the updated comment information. (optional)
     * @return Call&lt;ProblemComment&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @PUT("problem/details/{problemId}/comments/{commentId}")
    Call<ProblemComment> updateComment(
            @Path("problemId") Long problemId, @Path("commentId") Long commentId, @retrofit2.http.Body ProblemComment body
    );

}
