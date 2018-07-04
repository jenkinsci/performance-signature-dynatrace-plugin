package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Map;

public class Result {
    @SerializedName("dataPoints")
    private Map<String, Map<Long, Double>> dataPoints;
    @SerializedName("timeseriesId")
    private String timeseriesId;
    @SerializedName("unit")
    private String unit;
    @SerializedName("entities")
    private Map<String, String> entities;
    @SerializedName("resolutionInMillisUTC")
    private Long resolutionInMillisUTC;
    @SerializedName("aggregationType")
    private Timeseries.AggregationEnum aggregationType;

    public Map<String, Map<Long, Double>> getDataPoints() {
        return dataPoints;
    }

    public String getTimeseriesId() {
        return timeseriesId;
    }

    public String getUnit() {
        return unit;
    }

    public Map<String, String> getEntities() {
        return entities;
    }

    public Long getResolutionInMillisUTC() {
        return resolutionInMillisUTC;
    }

    public Timeseries.AggregationEnum getAggregationType() {
        return aggregationType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("dataPoints", dataPoints)
                .append("timeseriesId", timeseriesId)
                .append("unit", unit)
                .append("entities", entities)
                .append("resolutionInMillisUTC", resolutionInMillisUTC)
                .append("aggregationType", aggregationType).toString();
    }

    // Add a container for the root element
    public static class Container {
        @SerializedName("result")
        public Result result;
    }
}
