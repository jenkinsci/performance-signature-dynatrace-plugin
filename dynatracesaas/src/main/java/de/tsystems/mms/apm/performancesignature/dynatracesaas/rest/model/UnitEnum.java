package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Units of the metric.
 */
@JsonAdapter(UnitEnum.Adapter.class)
public enum UnitEnum {
    NANOSECOND("NanoSecond"),
    MICROSECOND("MicroSecond"),
    MILLISECOND("MilliSecond"),
    SECOND("Second"),
    MINUTE("Minute"),
    HOUR("Hour"),
    BIT("Bit"),
    BYTE("Byte"),
    KILOBYTE("KiloByte"),
    KIBIBYTE("KibiByte"),
    MEGABYTE("MegaByte"),
    MEBIBYTE("MebiByte"),
    GIGABYTE("GigaByte"),
    GIBIBYTE("GibiByte"),
    BYTEPERSECOND("BytePerSecond"),
    BYTEPERMINUTE("BytePerMinute"),
    BITPERSECOND("BitPerSecond"),
    BITPERMINUTE("BitPerMinute"),
    KILOBYTEPERSECOND("KiloBytePerSecond"),
    KILOBYTEPERMINUTE("KiloBytePerMinute"),
    KIBIBYTEPERSECOND("KibiBytePerSecond"),
    KIBIBYTEPERMINUTE("KibiBytePerMinute"),
    MEGABYTEPERSECOND("MegaBytePerSecond"),
    MEGABYTEPERMINUTE("MegaBytePerMinute"),
    MEBIBYTEPERSECOND("MebiBytePerSecond"),
    MEBIBYTEPERMINUTE("MebiBytePerMinute"),
    RATIO("Ratio"),
    PERCENT("Percent"),
    PROMILLE("Promille"),
    COUNT("Count"),
    PERSECOND("PerSecond"),
    PERMINUTE("PerMinute"),
    STATE("State"),
    UNSPECIFIED("Unspecified"),
    NOTAPPLICABLE("NotApplicable");

    private String value;

    UnitEnum(String value) {
        this.value = value;
    }

    public static UnitEnum fromValue(String text) {
        if (text != null && text.contains("(")) {
            text = text.substring(0, text.indexOf('(') - 1);
        }
        String finalText = text;
        return Arrays.stream(UnitEnum.values()).filter(b -> b.value.equals(finalText)).findFirst().orElse(null);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
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
