/*
 * Copyright (c) 2014-2018 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env1.model;

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
