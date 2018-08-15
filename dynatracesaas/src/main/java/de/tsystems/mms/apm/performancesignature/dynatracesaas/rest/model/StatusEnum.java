package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;

/**
 * The status of the event.
 */
@JsonAdapter(StatusEnum.Adapter.class)
public enum StatusEnum {
    OPEN("OPEN"),
    CLOSED("CLOSED");

    private final String value;

    StatusEnum(String value) {
        this.value = value;
    }

    public static StatusEnum fromValue(String text) {
        return Arrays.stream(StatusEnum.values()).filter(b -> b.value.equals(text)).findFirst().orElse(null);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static class Adapter extends TypeAdapter<StatusEnum> {
        @Override
        public void write(final JsonWriter jsonWriter, final StatusEnum enumeration) throws IOException {
            jsonWriter.value(enumeration.getValue());
        }

        @Override
        public StatusEnum read(final JsonReader jsonReader) throws IOException {
            String value = jsonReader.nextString();
            return StatusEnum.fromValue(value);
        }
    }
}
