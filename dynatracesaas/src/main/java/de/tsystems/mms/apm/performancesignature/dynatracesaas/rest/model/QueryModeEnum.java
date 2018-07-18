package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Defines the type of result that the call should return. Valid result modes are: series: returns all the data points of the metric in the specified timeframe. total: returns one scalar value for the specified timeframe.   By default, the series mode is used.
 */
@JsonAdapter(QueryModeEnum.Adapter.class)
public enum QueryModeEnum {
    SERIES("SERIES"),
    TOTAL("TOTAL");

    private String value;

    QueryModeEnum(String value) {
        this.value = value;
    }

    public static QueryModeEnum fromValue(String text) {
        return Arrays.stream(QueryModeEnum.values()).filter(b -> b.value.equals(text)).findFirst().orElse(null);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static class Adapter extends TypeAdapter<QueryModeEnum> {
        @Override
        public void write(final JsonWriter jsonWriter, final QueryModeEnum enumeration) throws IOException {
            jsonWriter.value(enumeration.getValue());
        }

        @Override
        public QueryModeEnum read(final JsonReader jsonReader) throws IOException {
            String value = jsonReader.nextString();
            return QueryModeEnum.fromValue(value);
        }
    }
}
