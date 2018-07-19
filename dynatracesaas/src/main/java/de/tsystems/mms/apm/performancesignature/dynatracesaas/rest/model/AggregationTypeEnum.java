package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Gets or Sets aggregationTypes
 */
@JsonAdapter(AggregationTypeEnum.Adapter.class)
public enum AggregationTypeEnum {
    MIN("MIN"),
    MAX("MAX"),
    SUM("SUM"),
    AVG("AVG"),
    MEDIAN("MEDIAN"),
    COUNT("COUNT"),
    PERCENTILE("PERCENTILE");

    private String value;

    AggregationTypeEnum(String value) {
        this.value = value;
    }

    public static AggregationTypeEnum fromValue(String text) {
        return Arrays.stream(AggregationTypeEnum.values()).filter(b -> b.value.equalsIgnoreCase(text)).findFirst().orElse(null);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static class Adapter extends TypeAdapter<AggregationTypeEnum> {
        @Override
        public void write(final JsonWriter jsonWriter, final AggregationTypeEnum enumeration) throws IOException {
            jsonWriter.value(enumeration.getValue());
        }

        @Override
        public AggregationTypeEnum read(final JsonReader jsonReader) throws IOException {
            String value = jsonReader.nextString();
            return AggregationTypeEnum.fromValue(value);
        }
    }
}
