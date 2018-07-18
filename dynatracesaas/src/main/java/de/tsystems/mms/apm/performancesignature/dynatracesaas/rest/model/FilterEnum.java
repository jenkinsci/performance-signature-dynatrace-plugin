package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;

/**
 * The feature, where the metric originates.
 */
@JsonAdapter(FilterEnum.Adapter.class)
public enum FilterEnum {
    ALL("ALL"),
    PLUGIN("PLUGIN"),
    REMOTE_PLUGIN("REMOTE_PLUGIN"),
    BUILTIN("BUILTIN"),
    CUSTOM("CUSTOM");

    private String value;

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
            return FilterEnum.fromValue(value);
        }
    }
}
