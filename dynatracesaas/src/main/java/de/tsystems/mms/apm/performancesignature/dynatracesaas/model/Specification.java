package de.tsystems.mms.apm.performancesignature.dynatracesaas.model;

import com.google.gson.annotations.SerializedName;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.AggregationTypeEnum;

public class Specification {
    @SerializedName("timeseriesId")
    private String timeseriesId;
    @SerializedName("aggregation")
    private AggregationTypeEnum aggregation;
    @SerializedName("threshold")
    private Double threshold;

    public Specification() {
    }

    public Specification(String timeseriesId) {
        this.timeseriesId = timeseriesId;
    }

    public String getTimeseriesId() {
        return timeseriesId;
    }

    public AggregationTypeEnum getAggregation() {
        return aggregation;
    }

    public Double getThreshold() {
        return threshold;
    }
}
