/*
 * Dynatrace Environment API
 *  Documentation of the Dynatrace Environment API v2. Resources here generally supersede those in v1. Migration of resources from v1 is in progress.   If you miss a resource, consider using the Dynatrace Environment API v1. To read about use cases and examples, see [Dynatrace Documentation](https://dt-url.net/2u23k1k) .  Notes about compatibility: * Operations marked as early adopter or preview may be changed in non-compatible ways, although we try to avoid this. * We may add new enum constants without incrementing the API version; thus, clients need to handle unknown enum constants gracefully.
 *
 * The version of the OpenAPI document: 2.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env2.model;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Data points per dimension of a metric.   The data is represented by two arrays of the same length: **timestamps** and **values**. Entries of the same index from both arrays form a timestamped data point.
 */
@ApiModel(description = "Data points per dimension of a metric.   The data is represented by two arrays of the same length: **timestamps** and **values**. Entries of the same index from both arrays form a timestamped data point.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class MetricSeries {
  public static final String SERIALIZED_NAME_DIMENSION_MAP = "dimensionMap";
  public static final String SERIALIZED_NAME_TIMESTAMPS = "timestamps";
  public static final String SERIALIZED_NAME_DIMENSIONS = "dimensions";
  public static final String SERIALIZED_NAME_VALUES = "values";
  @SerializedName(SERIALIZED_NAME_DIMENSION_MAP)
  private Map<String, String> dimensionMap = new HashMap<>();
  @SerializedName(SERIALIZED_NAME_DIMENSIONS)
  private List<String> dimensions = null;
  @SerializedName(SERIALIZED_NAME_TIMESTAMPS)
  private List<Long> timestamps = null;
  @SerializedName(SERIALIZED_NAME_VALUES)
  private List<Double> values = null;

  public MetricSeries dimensionMap(Map<String, String> dimensionMap) {

    this.dimensionMap = dimensionMap;
    return this;
  }

  public MetricSeries putDimensionMapItem(String key, String dimensionMapItem) {
    this.dimensionMap.put(key, dimensionMapItem);
    return this;
  }

  /**
   * Get dimensionMap
   *
   * @return dimensionMap
   **/
  @ApiModelProperty(required = true, value = "")

  public Map<String, String> getDimensionMap() {
    return dimensionMap;
  }

  public void setDimensionMap(Map<String, String> dimensionMap) {
    this.dimensionMap = dimensionMap;
  }

  public MetricSeries timestamps(List<Long> timestamps) {

    this.timestamps = timestamps;
    return this;
  }

  public MetricSeries addTimestampsItem(Long timestampsItem) {
    if (this.timestamps == null) {
      this.timestamps = new ArrayList<>();
    }
    this.timestamps.add(timestampsItem);
    return this;
  }

  /**
   * A list of timestamps of data points.   The value of data point for each time from this array is located in **values** array at the same index.
   *
   * @return timestamps
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "A list of timestamps of data points.   The value of data point for each time from this array is located in **values** array at the same index.")

  public List<Long> getTimestamps() {
    return timestamps;
  }

  public void setTimestamps(List<Long> timestamps) {
    this.timestamps = timestamps;
  }

  public MetricSeries dimensions(List<String> dimensions) {

    this.dimensions = dimensions;
    return this;
  }

  public MetricSeries addDimensionsItem(String dimensionsItem) {
    if (this.dimensions == null) {
      this.dimensions = new ArrayList<>();
    }
    this.dimensions.add(dimensionsItem);
    return this;
  }

  /**
   * The ordered list of dimensions to which the data point list belongs.    Each metric can have a certain number of dimensions. Dimensions exceeding this number are aggregated into one, which is shown as &#x60;null&#x60; here.
   *
   * @return dimensions
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The ordered list of dimensions to which the data point list belongs.    Each metric can have a certain number of dimensions. Dimensions exceeding this number are aggregated into one, which is shown as `null` here.")

  public List<String> getDimensions() {
    return dimensions;
  }

  public void setDimensions(List<String> dimensions) {
    this.dimensions = dimensions;
  }

  public MetricSeries values(List<Double> values) {

    this.values = values;
    return this;
  }

  public MetricSeries addValuesItem(Double valuesItem) {
    if (this.values == null) {
      this.values = new ArrayList<>();
    }
    this.values.add(valuesItem);
    return this;
  }

  /**
   * A list of values of data points.   The timestamp of data point for each value from this array is located in **timestamps** array at the same index.
   *
   * @return values
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "A list of values of data points.   The timestamp of data point for each value from this array is located in **timestamps** array at the same index.")

  public List<Double> getValues() {
    return values;
  }

  public void setValues(List<Double> values) {
    this.values = values;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MetricSeries metricSeries = (MetricSeries) o;
    return Objects.equals(this.dimensionMap, metricSeries.dimensionMap) &&
            Objects.equals(this.timestamps, metricSeries.timestamps) &&
            Objects.equals(this.dimensions, metricSeries.dimensions) &&
            Objects.equals(this.values, metricSeries.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dimensionMap, timestamps, dimensions, values);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MetricSeries {\n");
    sb.append("    dimensionMap: ").append(toIndentedString(dimensionMap)).append("\n");
    sb.append("    timestamps: ").append(toIndentedString(timestamps)).append("\n");
    sb.append("    dimensions: ").append(toIndentedString(dimensions)).append("\n");
    sb.append("    values: ").append(toIndentedString(values)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
