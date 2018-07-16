package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Timeseries {

    @SerializedName("timeseriesId")
    private String timeseriesId;
    @SerializedName("displayName")
    private String displayName;
    @SerializedName("dimensions")
    private List<String> dimensions;
    @SerializedName("aggregationTypes")
    private List<AggregationEnum> aggregationTypes;
    @SerializedName("unit")
    private String unit;
    @SerializedName("filter")
    private FilterEnum filter;
    @SerializedName("detailedSource")
    private String detailedSource;
    @SerializedName("pluginId")
    private String pluginId;
    @SerializedName("types")
    private List<String> types;

    public String getTimeseriesId() {
        return timeseriesId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getDimensions() {
        return dimensions;
    }

    public List<AggregationEnum> getAggregationTypes() {
        return aggregationTypes;
    }

    public String getUnit() {
        return unit;
    }

    public FilterEnum getFilter() {
        return filter;
    }

    public String getDetailedSource() {
        return detailedSource;
    }

    public String getPluginId() {
        return pluginId;
    }

    public List<String> getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("timeseriesId", timeseriesId)
                .append("displayName", displayName)
                .append("dimensions", dimensions)
                .append("aggregationTypes", aggregationTypes)
                .append("unit", unit)
                .append("filter", filter)
                .append("detailedSource", detailedSource)
                .append("pluginId", pluginId)
                .append("types", types).toString();
    }

    /**
     * Gets or Sets filter
     */
    @JsonAdapter(FilterEnum.Adapter.class)
    public enum FilterEnum {
        BUILTIN("BUILTIN"),
        PLUGIN("PLUGIN"),
        CUSTOM("CUSTOM");

        private final String value;

        FilterEnum(String value) {
            this.value = value;
        }

        public static FilterEnum fromValue(String text) {
            return Arrays.stream(FilterEnum.values()).filter(b -> b.value.equals(text)).findFirst().orElse(null);
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static class Adapter extends TypeAdapter<FilterEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final FilterEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public FilterEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return FilterEnum.fromValue(String.valueOf(value));
            }
        }
    }

    /**
     * Gets or Sets aggregation
     */
    @JsonAdapter(AggregationEnum.Adapter.class)
    public enum AggregationEnum {
        MIN("MIN"),
        MAX("MAX"),
        SUM("SUM"),
        AVG("AVG"),
        //MEDIAN("MEDIAN"),
        COUNT("COUNT");

        private final String value;

        AggregationEnum(String value) {
            this.value = value;
        }

        public static AggregationEnum fromValue(String text) {
            return Arrays.stream(AggregationEnum.values()).filter(b -> b.value.equals(text)).findFirst().orElse(null);
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static class Adapter extends TypeAdapter<AggregationEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final AggregationEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public AggregationEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return AggregationEnum.fromValue(String.valueOf(value));
            }
        }
    }
}
