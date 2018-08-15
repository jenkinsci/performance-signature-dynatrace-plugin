package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;

/**
 * The impact level of the event.
 */
@JsonAdapter(ImpactLevelEnum.Adapter.class)
public enum ImpactLevelEnum {
    INFRASTRUCTURE("INFRASTRUCTURE"),
    SERVICE("SERVICE"),
    APPLICATION("APPLICATION"),
    ENVIRONMENT("ENVIRONMENT");

    private final String value;

    ImpactLevelEnum(String value) {
        this.value = value;
    }

    public static ImpactLevelEnum fromValue(String text) {
        return Arrays.stream(ImpactLevelEnum.values()).filter(b -> b.value.equals(text)).findFirst().orElse(null);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static class Adapter extends TypeAdapter<ImpactLevelEnum> {
        @Override
        public void write(final JsonWriter jsonWriter, final ImpactLevelEnum enumeration) throws IOException {
            jsonWriter.value(enumeration.getValue());
        }

        @Override
        public ImpactLevelEnum read(final JsonReader jsonReader) throws IOException {
            String value = jsonReader.nextString();
            return ImpactLevelEnum.fromValue(value);
        }
    }
}
