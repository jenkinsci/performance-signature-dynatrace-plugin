package de.tsystems.mms.apm.performancesignature.dynatracesaas.model;

import com.google.gson.annotations.SerializedName;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.AggregationTypeEnum;
import org.apache.commons.lang.builder.ToStringBuilder;

public class SpecificationTM {
    @SerializedName("timeseriesId")
    private String timeseriesId;
    @SerializedName("aggregation")
    private AggregationTypeEnum aggregation;
    @SerializedName("tags")
    private String tags;
    @SerializedName("entityIds")
    private String entityIds;
    @SerializedName("tolerateBound")
    private double tolerateBound;
    @SerializedName("frustrateBound")
    private double frustrateBound;

    /**
     * No args constructor for use in serialization
     */
    public SpecificationTM() {
    }

    /**
     * @param tags
     * @param aggregation
     * @param tolerateBound
     * @param entityIds
     * @param timeseriesId
     * @param frustrateBound
     */
    public SpecificationTM(String timeseriesId, AggregationTypeEnum aggregation, String tags, String entityIds, double tolerateBound, double frustrateBound) {
        this.timeseriesId = timeseriesId;
        this.aggregation = aggregation;
        this.tags = tags;
        this.entityIds = entityIds;
        this.tolerateBound = tolerateBound;
        this.frustrateBound = frustrateBound;
    }

    public SpecificationTM(String metricId) {
        this.timeseriesId = metricId;
    }

    public String getTimeseriesId() {
        return timeseriesId;
    }

    public void setTimeseriesId(String timeseriesId) {
        this.timeseriesId = timeseriesId;
    }

    public AggregationTypeEnum getAggregation() {
        if (aggregation == null) return AggregationTypeEnum.AVG;
        return aggregation;
    }

    public void setAggregation(AggregationTypeEnum aggregation) {
        this.aggregation = aggregation;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getEntityIds() {
        return entityIds;
    }

    public void setEntityIds(String entityIds) {
        this.entityIds = entityIds;
    }

    public double getTolerateBound() {
        return tolerateBound;
    }

    public void setTolerateBound(double tolerateBound) {
        this.tolerateBound = tolerateBound;
    }

    public double getFrustrateBound() {
        return frustrateBound;
    }

    public void setFrustrateBound(double frustrateBound) {
        this.frustrateBound = frustrateBound;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("timeseriesId", timeseriesId).append("aggregation", aggregation).append("tags", tags).append("entityIds", entityIds).append("tolerateBound", tolerateBound).append("frustrateBound", frustrateBound).toString();
    }
}
