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
import java.util.List;
import java.util.Objects;

/**
 * A list of metrics along with their descriptors.
 */
@ApiModel(description = "A list of metrics along with their descriptors.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class MetricDescriptorCollection {
  public static final String SERIALIZED_NAME_NEXT_PAGE_KEY = "nextPageKey";
  public static final String SERIALIZED_NAME_TOTAL_COUNT = "totalCount";
  public static final String SERIALIZED_NAME_METRICS = "metrics";
  public static final String SERIALIZED_NAME_WARNINGS = "warnings";
  @SerializedName(SERIALIZED_NAME_NEXT_PAGE_KEY)
  private String nextPageKey;
  @SerializedName(SERIALIZED_NAME_TOTAL_COUNT)
  private Long totalCount;
  @SerializedName(SERIALIZED_NAME_METRICS)
  private List<MetricDescriptor> metrics = null;
  @SerializedName(SERIALIZED_NAME_WARNINGS)
  private List<String> warnings = null;

  public MetricDescriptorCollection nextPageKey(String nextPageKey) {

    this.nextPageKey = nextPageKey;
    return this;
  }

  /**
   * The cursor for the next page of results. Has the value of &#x60;null&#x60; on the last page.   Use it in the **nextPageKey** query parameter to obtain subsequent pages of the result.
   *
   * @return nextPageKey
   **/
  @ApiModelProperty(required = true, value = "The cursor for the next page of results. Has the value of `null` on the last page.   Use it in the **nextPageKey** query parameter to obtain subsequent pages of the result.")

  public String getNextPageKey() {
    return nextPageKey;
  }

  public void setNextPageKey(String nextPageKey) {
    this.nextPageKey = nextPageKey;
  }

  public MetricDescriptorCollection totalCount(Long totalCount) {

    this.totalCount = totalCount;
    return this;
  }

  /**
   * The estimated number of metrics in the result.
   *
   * @return totalCount
   **/
  @ApiModelProperty(required = true, value = "The estimated number of metrics in the result.")

  public Long getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(Long totalCount) {
    this.totalCount = totalCount;
  }

  public MetricDescriptorCollection metrics(List<MetricDescriptor> metrics) {

    this.metrics = metrics;
    return this;
  }

  public MetricDescriptorCollection addMetricsItem(MetricDescriptor metricsItem) {
    if (this.metrics == null) {
      this.metrics = new ArrayList<>();
    }
    this.metrics.add(metricsItem);
    return this;
  }

  /**
   * A list of metric along with their descriptors
   *
   * @return metrics
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "A list of metric along with their descriptors")

  public List<MetricDescriptor> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<MetricDescriptor> metrics) {
    this.metrics = metrics;
  }

  public MetricDescriptorCollection warnings(List<String> warnings) {

    this.warnings = warnings;
    return this;
  }

  public MetricDescriptorCollection addWarningsItem(String warningsItem) {
    if (this.warnings == null) {
      this.warnings = new ArrayList<>();
    }
    this.warnings.add(warningsItem);
    return this;
  }

  /**
   * A list of potential warnings about the query. For example deprecated feature usage etc.
   *
   * @return warnings
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "A list of potential warnings about the query. For example deprecated feature usage etc.")

  public List<String> getWarnings() {
    return warnings;
  }

  public void setWarnings(List<String> warnings) {
    this.warnings = warnings;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MetricDescriptorCollection metricDescriptorCollection = (MetricDescriptorCollection) o;
    return Objects.equals(this.nextPageKey, metricDescriptorCollection.nextPageKey) &&
            Objects.equals(this.totalCount, metricDescriptorCollection.totalCount) &&
            Objects.equals(this.metrics, metricDescriptorCollection.metrics) &&
            Objects.equals(this.warnings, metricDescriptorCollection.warnings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nextPageKey, totalCount, metrics, warnings);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MetricDescriptorCollection {\n");
    sb.append("    nextPageKey: ").append(toIndentedString(nextPageKey)).append("\n");
    sb.append("    totalCount: ").append(toIndentedString(totalCount)).append("\n");
    sb.append("    metrics: ").append(toIndentedString(metrics)).append("\n");
    sb.append("    warnings: ").append(toIndentedString(warnings)).append("\n");
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

