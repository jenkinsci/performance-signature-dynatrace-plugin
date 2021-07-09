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

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The descriptor of a metric.
 */
@ApiModel(description = "The descriptor of a metric.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class MetricDescriptor {
  public static final String SERIALIZED_NAME_LAST_WRITTEN = "lastWritten";
  public static final String SERIALIZED_NAME_LATENCY = "latency";

  public static final String SERIALIZED_NAME_DDU_BILLABLE = "dduBillable";
  public static final String SERIALIZED_NAME_METRIC_VALUE_TYPE = "metricValueType";

  public static final String SERIALIZED_NAME_ENTITY_TYPE = "entityType";
  public static final String SERIALIZED_NAME_ROOT_CAUSE_RELEVANT = "rootCauseRelevant";

  public static final String SERIALIZED_NAME_MINIMUM_VALUE = "minimumValue";
  public static final String SERIALIZED_NAME_MAXIMUM_VALUE = "maximumValue";
  public static final String SERIALIZED_NAME_METRIC_ID = "metricId";
  public static final String SERIALIZED_NAME_DIMENSION_DEFINITIONS = "dimensionDefinitions";

  public static final String SERIALIZED_NAME_DEFAULT_AGGREGATION = "defaultAggregation";
  public static final String SERIALIZED_NAME_IMPACT_RELEVANT = "impactRelevant";
  public static final String SERIALIZED_NAME_AGGREGATION_TYPES = "aggregationTypes";
  public static final String SERIALIZED_NAME_DISPLAY_NAME = "displayName";
  public static final String SERIALIZED_NAME_DESCRIPTION = "description";
  public static final String SERIALIZED_NAME_TAGS = "tags";
  public static final String SERIALIZED_NAME_TRANSFORMATIONS = "transformations";
  public static final String SERIALIZED_NAME_UNIT = "unit";
  public static final String SERIALIZED_NAME_WARNINGS = "warnings";
  public static final String SERIALIZED_NAME_CREATED = "created";
  @SerializedName(SERIALIZED_NAME_METRIC_VALUE_TYPE)
  private MetricValueType metricValueType;
  @SerializedName(SERIALIZED_NAME_METRIC_ID)
  private String metricId;
  @SerializedName(SERIALIZED_NAME_LAST_WRITTEN)
  private Long lastWritten;
  @SerializedName(SERIALIZED_NAME_IMPACT_RELEVANT)
  private Boolean impactRelevant;
  @SerializedName(SERIALIZED_NAME_DDU_BILLABLE)
  private Boolean dduBillable;
  @SerializedName(SERIALIZED_NAME_ENTITY_TYPE)
  private Set<String> entityType = null;
  @SerializedName(SERIALIZED_NAME_AGGREGATION_TYPES)
  private Set<AggregationTypesEnum> aggregationTypes = null;
  @SerializedName(SERIALIZED_NAME_MINIMUM_VALUE)
  private Double minimumValue;
  @SerializedName(SERIALIZED_NAME_DISPLAY_NAME)
  private String displayName;
  @SerializedName(SERIALIZED_NAME_LATENCY)
  private Long latency;
  @SerializedName(SERIALIZED_NAME_DESCRIPTION)
  private String description;
  @SerializedName(SERIALIZED_NAME_DEFAULT_AGGREGATION)
  private MetricDefaultAggregation defaultAggregation;
  @SerializedName(SERIALIZED_NAME_ROOT_CAUSE_RELEVANT)
  private Boolean rootCauseRelevant;
  @SerializedName(SERIALIZED_NAME_MAXIMUM_VALUE)
  private Double maximumValue;
  @SerializedName(SERIALIZED_NAME_DIMENSION_DEFINITIONS)
  private List<MetricDimensionDefinition> dimensionDefinitions = null;
  @SerializedName(SERIALIZED_NAME_TRANSFORMATIONS)
  private Set<TransformationsEnum> transformations = null;
  @SerializedName(SERIALIZED_NAME_TAGS)
  private Set<String> tags = null;
  @SerializedName(SERIALIZED_NAME_WARNINGS)
  private List<String> warnings = null;
  @SerializedName(SERIALIZED_NAME_UNIT)
  private UnitEnum unit;
  @SerializedName(SERIALIZED_NAME_CREATED)
  private Long created;

  public MetricDescriptor lastWritten(Long lastWritten) {

    this.lastWritten = lastWritten;
    return this;
  }

  /**
   * The timestamp when the metric was last written.   Has the value of &#x60;null&#x60; for metric expressions or if the data has never been written.
   *
   * @return lastWritten
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The timestamp when the metric was last written.   Has the value of `null` for metric expressions or if the data has never been written.")

  public Long getLastWritten() {
    return lastWritten;
  }

  public void setLastWritten(Long lastWritten) {
    this.lastWritten = lastWritten;
  }

  public MetricDescriptor dduBillable(Boolean dduBillable) {

    this.dduBillable = dduBillable;
    return this;
  }

  /**
   * If &#x60;true&#x60; the usage of metric consumes [Davis data units](https://dt-url.net/ddu).    Metric expressions don&#39;t return this field.
   *
   * @return dduBillable
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "If `true` the usage of metric consumes [Davis data units](https://dt-url.net/ddu).    Metric expressions don't return this field.")

  public Boolean getDduBillable() {
    return dduBillable;
  }

  public void setDduBillable(Boolean dduBillable) {
    this.dduBillable = dduBillable;
  }

  public MetricDescriptor entityType(Set<String> entityType) {

    this.entityType = entityType;
    return this;
  }

  /**
   * List of admissible primary entity types for this metric. Can be used for the &#x60;type&#x60; predicate in the &#x60;entitySelector&#x60;.
   *
   * @return entityType
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "List of admissible primary entity types for this metric. Can be used for the `type` predicate in the `entitySelector`.")

  public Set<String> getEntityType() {
    return entityType;
  }

  public void setEntityType(Set<String> entityType) {
    this.entityType = entityType;
  }

  public MetricDescriptor minimumValue(Double minimumValue) {

    this.minimumValue = minimumValue;
    return this;
  }

  /**
   * The minimum value of the metric.   Metric expressions don&#39;t return this field.
   *
   * @return minimumValue
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The minimum value of the metric.   Metric expressions don't return this field.")

  public Double getMinimumValue() {
    return minimumValue;
  }

  public void setMinimumValue(Double minimumValue) {
    this.minimumValue = minimumValue;
  }

  public MetricDescriptor addEntityTypeItem(String entityTypeItem) {
    if (this.entityType == null) {
      this.entityType = new LinkedHashSet<>();
    }
    this.entityType.add(entityTypeItem);
    return this;
  }

  public MetricDescriptor latency(Long latency) {

    this.latency = latency;
    return this;
  }

  /**
   * The latency (in minutes) to how long it takes before a new metric data point is available in Monitoring after it is written.   Metric expressions don&#39;t return this field.
   *
   * @return latency
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The latency (in minutes) to how long it takes before a new metric data point is available in Monitoring after it is written.   Metric expressions don't return this field.")

  public Long getLatency() {
    return latency;
  }

  public void setLatency(Long latency) {
    this.latency = latency;
  }

  public MetricDescriptor defaultAggregation(MetricDefaultAggregation defaultAggregation) {

    this.defaultAggregation = defaultAggregation;
    return this;
  }

  /**
   * Get defaultAggregation
   *
   * @return defaultAggregation
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public MetricDefaultAggregation getDefaultAggregation() {
    return defaultAggregation;
  }

  public void setDefaultAggregation(MetricDefaultAggregation defaultAggregation) {
    this.defaultAggregation = defaultAggregation;
  }

  public MetricDescriptor metricValueType(MetricValueType metricValueType) {

    this.metricValueType = metricValueType;
    return this;
  }

  /**
   * Get metricValueType
   *
   * @return metricValueType
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public MetricValueType getMetricValueType() {
    return metricValueType;
  }

  public void setMetricValueType(MetricValueType metricValueType) {
    this.metricValueType = metricValueType;
  }

  public MetricDescriptor rootCauseRelevant(Boolean rootCauseRelevant) {

    this.rootCauseRelevant = rootCauseRelevant;
    return this;
  }

  /**
   * The metric is (&#x60;true&#x60;) or is not (&#x60;false&#x60;) root cause relevant.   Metric expressions don&#39;t return this field.
   *
   * @return rootCauseRelevant
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The metric is (`true`) or is not (`false`) root cause relevant.   Metric expressions don't return this field.")

  public Boolean getRootCauseRelevant() {
    return rootCauseRelevant;
  }

  public void setRootCauseRelevant(Boolean rootCauseRelevant) {
    this.rootCauseRelevant = rootCauseRelevant;
  }

  public MetricDescriptor maximumValue(Double maximumValue) {

    this.maximumValue = maximumValue;
    return this;
  }

  /**
   * The maximum value of the metric.   Metric expressions don&#39;t return this field.
   *
   * @return maximumValue
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The maximum value of the metric.   Metric expressions don't return this field.")

  public Double getMaximumValue() {
    return maximumValue;
  }

  public void setMaximumValue(Double maximumValue) {
    this.maximumValue = maximumValue;
  }

  public MetricDescriptor metricId(String metricId) {

    this.metricId = metricId;
    return this;
  }

  /**
   * The fully qualified key of the metric.   If a transformation has been used it is reflected in the metric key.
   *
   * @return metricId
   **/
  @ApiModelProperty(required = true, value = "The fully qualified key of the metric.   If a transformation has been used it is reflected in the metric key.")

  public String getMetricId() {
    return metricId;
  }

  public void setMetricId(String metricId) {
    this.metricId = metricId;
  }

  public MetricDescriptor dimensionDefinitions(List<MetricDimensionDefinition> dimensionDefinitions) {

    this.dimensionDefinitions = dimensionDefinitions;
    return this;
  }

  /**
   * The fine metric division (for example, process group and process ID for some process-related metric).   For [ingested metrics](https://dt-url.net/5d63ic1), dimensions that doesn&#39;t have have any data within the last 15 days are omitted.
   *
   * @return dimensionDefinitions
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The fine metric division (for example, process group and process ID for some process-related metric).   For [ingested metrics](https://dt-url.net/5d63ic1), dimensions that doesn't have have any data within the last 15 days are omitted. ")

  public List<MetricDimensionDefinition> getDimensionDefinitions() {
    return dimensionDefinitions;
  }

  public void setDimensionDefinitions(List<MetricDimensionDefinition> dimensionDefinitions) {
    this.dimensionDefinitions = dimensionDefinitions;
  }

  public MetricDescriptor impactRelevant(Boolean impactRelevant) {

    this.impactRelevant = impactRelevant;
    return this;
  }

  /**
   * The metric is (&#x60;true&#x60;) or is not (&#x60;false&#x60;) impact relevant.   Metric expressions don&#39;t return this field.
   *
   * @return impactRelevant
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The metric is (`true`) or is not (`false`) impact relevant.   Metric expressions don't return this field.")

  public Boolean getImpactRelevant() {
    return impactRelevant;
  }

  public void setImpactRelevant(Boolean impactRelevant) {
    this.impactRelevant = impactRelevant;
  }

  public MetricDescriptor addDimensionDefinitionsItem(MetricDimensionDefinition dimensionDefinitionsItem) {
    if (this.dimensionDefinitions == null) {
      this.dimensionDefinitions = new ArrayList<>();
    }
    this.dimensionDefinitions.add(dimensionDefinitionsItem);
    return this;
  }

  public MetricDescriptor aggregationTypes(Set<AggregationTypesEnum> aggregationTypes) {

    this.aggregationTypes = aggregationTypes;
    return this;
  }

  public MetricDescriptor addAggregationTypesItem(AggregationTypesEnum aggregationTypesItem) {
    if (this.aggregationTypes == null) {
      this.aggregationTypes = new LinkedHashSet<>();
    }
    this.aggregationTypes.add(aggregationTypesItem);
    return this;
  }

  /**
   * The list of allowed aggregations for this metric.
   *
   * @return aggregationTypes
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The list of allowed aggregations for this metric.")

  public Set<AggregationTypesEnum> getAggregationTypes() {
    return aggregationTypes;
  }

  public void setAggregationTypes(Set<AggregationTypesEnum> aggregationTypes) {
    this.aggregationTypes = aggregationTypes;
  }

  public MetricDescriptor displayName(String displayName) {

    this.displayName = displayName;
    return this;
  }

  /**
   * The name of the metric in the user interface.
   *
   * @return displayName
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The name of the metric in the user interface.")

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public MetricDescriptor description(String description) {

    this.description = description;
    return this;
  }

  /**
   * A short description of the metric.
   *
   * @return description
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "A short description of the metric.")

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public MetricDescriptor tags(Set<String> tags) {

    this.tags = tags;
    return this;
  }

  public MetricDescriptor addTagsItem(String tagsItem) {
    if (this.tags == null) {
      this.tags = new LinkedHashSet<>();
    }
    this.tags.add(tagsItem);
    return this;
  }

  /**
   * The tags applied to the metric.    Metric expressions don&#39;t return this field.
   *
   * @return tags
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The tags applied to the metric.    Metric expressions don't return this field.")

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  public MetricDescriptor transformations(Set<TransformationsEnum> transformations) {

    this.transformations = transformations;
    return this;
  }

  /**
   * Transform operators that could be appended to the current transformation list. Must be enabled with the \&quot;fields\&quot; parameter on &#x60;/metrics&#x60; and is always present on &#x60;/metrics/{metricId}&#x60;.
   *
   * @return transformations
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "Transform operators that could be appended to the current transformation list. Must be enabled with the \"fields\" parameter on `/metrics` and is always present on `/metrics/{metricId}`.")

  public Set<TransformationsEnum> getTransformations() {
    return transformations;
  }

  public void setTransformations(Set<TransformationsEnum> transformations) {
    this.transformations = transformations;
  }

  public MetricDescriptor unit(UnitEnum unit) {

    this.unit = unit;
    return this;
  }

  /**
   * The unit of the metric.
   *
   * @return unit
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The unit of the metric.")

  public UnitEnum getUnit() {
    return unit;
  }

  public void setUnit(UnitEnum unit) {
    this.unit = unit;
  }

  public MetricDescriptor addTransformationsItem(TransformationsEnum transformationsItem) {
    if (this.transformations == null) {
      this.transformations = new LinkedHashSet<>();
    }
    this.transformations.add(transformationsItem);
    return this;
  }

  public MetricDescriptor warnings(List<String> warnings) {

    this.warnings = warnings;
    return this;
  }

  public MetricDescriptor addWarningsItem(String warningsItem) {
    if (this.warnings == null) {
      this.warnings = new ArrayList<>();
    }
    this.warnings.add(warningsItem);
    return this;
  }

  /**
   * A list of potential warnings that affect this ID. For example deprecated feature usage etc.
   *
   * @return warnings
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "A list of potential warnings that affect this ID. For example deprecated feature usage etc.")

  public List<String> getWarnings() {
    return warnings;
  }

  public void setWarnings(List<String> warnings) {
    this.warnings = warnings;
  }

  public MetricDescriptor created(Long created) {

    this.created = created;
    return this;
  }

  /**
   * The timestamp of metric creation.   Built-in metrics and metric expressions have the value of &#x60;null&#x60;.
   *
   * @return created
   **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The timestamp of metric creation.   Built-in metrics and metric expressions have the value of `null`.")

  public Long getCreated() {
    return created;
  }

  public void setCreated(Long created) {
    this.created = created;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MetricDescriptor metricDescriptor = (MetricDescriptor) o;
    return Objects.equals(this.lastWritten, metricDescriptor.lastWritten) &&
            Objects.equals(this.dduBillable, metricDescriptor.dduBillable) &&
            Objects.equals(this.entityType, metricDescriptor.entityType) &&
            Objects.equals(this.minimumValue, metricDescriptor.minimumValue) &&
            Objects.equals(this.latency, metricDescriptor.latency) &&
            Objects.equals(this.defaultAggregation, metricDescriptor.defaultAggregation) &&
            Objects.equals(this.metricValueType, metricDescriptor.metricValueType) &&
            Objects.equals(this.rootCauseRelevant, metricDescriptor.rootCauseRelevant) &&
            Objects.equals(this.maximumValue, metricDescriptor.maximumValue) &&
            Objects.equals(this.metricId, metricDescriptor.metricId) &&
            Objects.equals(this.dimensionDefinitions, metricDescriptor.dimensionDefinitions) &&
            Objects.equals(this.impactRelevant, metricDescriptor.impactRelevant) &&
            Objects.equals(this.aggregationTypes, metricDescriptor.aggregationTypes) &&
            Objects.equals(this.displayName, metricDescriptor.displayName) &&
            Objects.equals(this.description, metricDescriptor.description) &&
            Objects.equals(this.tags, metricDescriptor.tags) &&
            Objects.equals(this.transformations, metricDescriptor.transformations) &&
            Objects.equals(this.unit, metricDescriptor.unit) &&
            Objects.equals(this.warnings, metricDescriptor.warnings) &&
            Objects.equals(this.created, metricDescriptor.created);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lastWritten, dduBillable, entityType, minimumValue, latency, defaultAggregation, metricValueType, rootCauseRelevant, maximumValue, metricId, dimensionDefinitions, impactRelevant, aggregationTypes, displayName, description, tags, transformations, unit, warnings, created);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MetricDescriptor {\n");
    sb.append("    lastWritten: ").append(toIndentedString(lastWritten)).append("\n");
    sb.append("    dduBillable: ").append(toIndentedString(dduBillable)).append("\n");
    sb.append("    entityType: ").append(toIndentedString(entityType)).append("\n");
    sb.append("    minimumValue: ").append(toIndentedString(minimumValue)).append("\n");
    sb.append("    latency: ").append(toIndentedString(latency)).append("\n");
    sb.append("    defaultAggregation: ").append(toIndentedString(defaultAggregation)).append("\n");
    sb.append("    metricValueType: ").append(toIndentedString(metricValueType)).append("\n");
    sb.append("    rootCauseRelevant: ").append(toIndentedString(rootCauseRelevant)).append("\n");
    sb.append("    maximumValue: ").append(toIndentedString(maximumValue)).append("\n");
    sb.append("    metricId: ").append(toIndentedString(metricId)).append("\n");
    sb.append("    dimensionDefinitions: ").append(toIndentedString(dimensionDefinitions)).append("\n");
    sb.append("    impactRelevant: ").append(toIndentedString(impactRelevant)).append("\n");
    sb.append("    aggregationTypes: ").append(toIndentedString(aggregationTypes)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    transformations: ").append(toIndentedString(transformations)).append("\n");
    sb.append("    unit: ").append(toIndentedString(unit)).append("\n");
    sb.append("    warnings: ").append(toIndentedString(warnings)).append("\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
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

  /**
   * Gets or Sets aggregationTypes
   */
  @JsonAdapter(AggregationTypesEnum.Adapter.class)
  public enum AggregationTypesEnum {
    AUTO("auto"),

    AVG("avg"),

    COUNT("count"),

    MAX("max"),

    MEDIAN("median"),

    MIN("min"),

    PERCENTILE("percentile"),

    SUM("sum"),

    VALUE("value");

    private String value;

    AggregationTypesEnum(String value) {
      this.value = value;
    }

    public static AggregationTypesEnum fromValue(String value) {
      for (AggregationTypesEnum b : AggregationTypesEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static class Adapter extends TypeAdapter<AggregationTypesEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final AggregationTypesEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public AggregationTypesEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return AggregationTypesEnum.fromValue(value);
      }
    }
  }

  /**
   * Gets or Sets transformations
   */
  @JsonAdapter(TransformationsEnum.Adapter.class)
  public enum TransformationsEnum {
    DEFAULT("default"),

    FILTER("filter"),

    FOLD("fold"),

    LAST("last"),

    LIMIT("limit"),

    MERGE("merge"),

    NAMES("names"),

    PARENTS("parents"),

    RATE("rate"),

    SORT("sort"),

    SPLITBY("splitBy"),

    TIMESHIFT("timeshift");

    private String value;

    TransformationsEnum(String value) {
      this.value = value;
    }

    public static TransformationsEnum fromValue(String value) {
      for (TransformationsEnum b : TransformationsEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static class Adapter extends TypeAdapter<TransformationsEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final TransformationsEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public TransformationsEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return TransformationsEnum.fromValue(value);
      }
    }
  }

  /**
   * The unit of the metric.
   */
  @JsonAdapter(UnitEnum.Adapter.class)
  public enum UnitEnum {
    BIT("Bit"),

    BITPERHOUR("BitPerHour"),

    BITPERMINUTE("BitPerMinute"),

    BITPERSECOND("BitPerSecond"),

    BYTE("Byte"),

    BYTEPERHOUR("BytePerHour"),

    BYTEPERMINUTE("BytePerMinute"),

    BYTEPERSECOND("BytePerSecond"),

    CORES("Cores"),

    COUNT("Count"),

    DAY("Day"),

    DECIBELMILLIWATT("DecibelMilliWatt"),

    GIBIBYTE("GibiByte"),

    GIGA("Giga"),

    GIGABYTE("GigaByte"),

    HOUR("Hour"),

    KIBIBYTE("KibiByte"),

    KIBIBYTEPERHOUR("KibiBytePerHour"),

    KIBIBYTEPERMINUTE("KibiBytePerMinute"),

    KIBIBYTEPERSECOND("KibiBytePerSecond"),

    KILO("Kilo"),

    KILOBYTE("KiloByte"),

    KILOBYTEPERHOUR("KiloBytePerHour"),

    KILOBYTEPERMINUTE("KiloBytePerMinute"),

    KILOBYTEPERSECOND("KiloBytePerSecond"),

    MSU("MSU"),

    MEBIBYTE("MebiByte"),

    MEBIBYTEPERHOUR("MebiBytePerHour"),

    MEBIBYTEPERMINUTE("MebiBytePerMinute"),

    MEBIBYTEPERSECOND("MebiBytePerSecond"),

    MEGA("Mega"),

    MEGABYTE("MegaByte"),

    MEGABYTEPERHOUR("MegaBytePerHour"),

    MEGABYTEPERMINUTE("MegaBytePerMinute"),

    MEGABYTEPERSECOND("MegaBytePerSecond"),

    MICROSECOND("MicroSecond"),

    MILLICORES("MilliCores"),

    MILLISECOND("MilliSecond"),

    MILLISECONDPERMINUTE("MilliSecondPerMinute"),

    MINUTE("Minute"),

    MONTH("Month"),

    NANOSECOND("NanoSecond"),

    NANOSECONDPERMINUTE("NanoSecondPerMinute"),

    NOTAPPLICABLE("NotApplicable"),

    PERHOUR("PerHour"),

    PERMINUTE("PerMinute"),

    PERSECOND("PerSecond"),

    PERCENT("Percent"),

    PIXEL("Pixel"),

    PROMILLE("Promille"),

    RATIO("Ratio"),

    SECOND("Second"),

    STATE("State"),

    UNSPECIFIED("Unspecified"),

    WEEK("Week"),

    YEAR("Year");

    private String value;

    UnitEnum(String value) {
      this.value = value;
    }

    public static UnitEnum fromValue(String value) {
      for (UnitEnum b : UnitEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static class Adapter extends TypeAdapter<UnitEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final UnitEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public UnitEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return UnitEnum.fromValue(value);
      }
    }
  }

}
