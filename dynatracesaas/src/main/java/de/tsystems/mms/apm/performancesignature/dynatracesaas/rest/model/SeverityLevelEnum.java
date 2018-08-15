package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;

/**
 * The severity of the event.
 */
@JsonAdapter(SeverityLevelEnum.Adapter.class)
public enum SeverityLevelEnum {
    AVAILABILITY("AVAILABILITY"),
    ERROR("ERROR"),
    PERFORMANCE("PERFORMANCE"),
    RESOURCE_CONTENTION("RESOURCE_CONTENTION"),
    CUSTOM_ALERT("CUSTOM_ALERT"),
    MONITORING_UNAVAILABLE("MONITORING_UNAVAILABLE");

    private final String value;

    SeverityLevelEnum(String value) {
        this.value = value;
    }

    public static SeverityLevelEnum fromValue(String text) {
        return Arrays.stream(SeverityLevelEnum.values()).filter(b -> b.value.equals(text)).findFirst().orElse(null);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static class Adapter extends TypeAdapter<SeverityLevelEnum> {
        @Override
        public void write(final JsonWriter jsonWriter, final SeverityLevelEnum enumeration) throws IOException {
            jsonWriter.value(enumeration.getValue());
        }

        @Override
        public SeverityLevelEnum read(final JsonReader jsonReader) throws IOException {
            String value = jsonReader.nextString();
            return SeverityLevelEnum.fromValue(value);
        }
    }
}
