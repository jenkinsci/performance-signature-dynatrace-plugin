package de.tsystems.mms.apm.performancesignature.dynatracesaas.model;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

public class Specification {
    @SerializedName("tolerateBound")
    private double tolerateBound;
    @SerializedName("frustrateBound")
    private double frustrateBound;
    @SerializedName("timeseries")
    private List<SpecificationTM> timeseries = null;

    /**
     * No args constructor for use in serialization
     */
    public Specification() {
    }

    /**
     * @param tolerateBound
     * @param timeseries
     * @param frustrateBound
     */
    public Specification(int tolerateBound, int frustrateBound, List<SpecificationTM> timeseries) {
        this.tolerateBound = tolerateBound;
        this.frustrateBound = frustrateBound;
        this.timeseries = timeseries;
    }

    public double getTolerateBound() {
        return tolerateBound;
    }

    public void setTolerateBound(int tolerateBound) {
        this.tolerateBound = tolerateBound;
    }

    public double getFrustrateBound() {
        return frustrateBound;
    }

    public void setFrustrateBound(int frustrateBound) {
        this.frustrateBound = frustrateBound;
    }

    public List<SpecificationTM> getTimeseries() {
        return timeseries;
    }

    public void setTimeseries(List<SpecificationTM> timeseries) {
        this.timeseries = timeseries;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("tolerateBound", tolerateBound).append("frustrateBound", frustrateBound).append("timeseries", timeseries).toString();
    }
}
