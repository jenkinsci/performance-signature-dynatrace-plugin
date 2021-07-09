/*
 * Copyright (c) 2014-2018 T-Systems Multimedia Solutions GmbH
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

package de.tsystems.mms.apm.performancesignature.dynatracesaas.model;

import com.google.gson.annotations.SerializedName;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env1.model.AggregationTypeEnum;
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
    @SerializedName(value = "lowerWarning", alternate = "lowerLimit")
    private Double lowerWarning;
    @SerializedName("lowerSevere")
    private Double lowerSevere;
    @SerializedName(value = "upperWarning", alternate = "upperLimit")
    private Double upperWarning;
    @SerializedName("upperSevere")
    private Double upperSevere;

    /**
     * No args constructor for use in serialization
     */
    public SpecificationTM() {
    }

    public SpecificationTM(String timeseriesId, AggregationTypeEnum aggregation, String tags, String entityIds,
                           Double lowerWarning, Double lowerSevere, Double upperWarning, Double upperSevere) {
        this.timeseriesId = timeseriesId;
        this.aggregation = aggregation;
        this.tags = tags;
        this.entityIds = entityIds;
        this.lowerWarning = lowerWarning;
        this.lowerSevere = lowerSevere;
        this.upperWarning = upperWarning;
        this.upperSevere = upperSevere;
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

    public Double getLowerWarning() {
        return lowerWarning;
    }

    public Double getLowerSevere() {
        return lowerSevere;
    }

    public Double getUpperWarning() {
        return upperWarning;
    }

    public Double getUpperSevere() {
        return upperSevere;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("timeseriesId", timeseriesId)
                .append("aggregation", aggregation)
                .append("tags", tags)
                .append("entityIds", entityIds)
                .append("lowerWarning", lowerWarning)
                .append("lowerSevere", lowerSevere)
                .append("upperWarning", upperWarning)
                .append("upperSevere", upperSevere)
                .toString();
    }
}
