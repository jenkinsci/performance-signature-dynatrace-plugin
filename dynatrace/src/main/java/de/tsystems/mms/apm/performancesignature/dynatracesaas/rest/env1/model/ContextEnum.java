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
 * The metric used in the event severity calculation.
 */
@JsonAdapter(ContextEnum.Adapter.class)
public enum ContextEnum {
    CPU_USAGE("CPU_USAGE"),
    MEMORY_USAGE("MEMORY_USAGE"),
    NETWORK_HIGH_RECEIVED_UTILIZATION_RATE("NETWORK_HIGH_RECEIVED_UTILIZATION_RATE"),
    NETWORK_HIGH_TRANSMITTED_UTILIZATION_RATE("NETWORK_HIGH_TRANSMITTED_UTILIZATION_RATE"),
    NETWORK_RECEIVED_ERROR_RATE("NETWORK_RECEIVED_ERROR_RATE"),
    NETWORK_TRANSMITTED_ERROR_RATE("NETWORK_TRANSMITTED_ERROR_RATE"),
    CPU_READY_TIME("CPU_READY_TIME"),
    MEMORY_SWAP_IN_RATE("MEMORY_SWAP_IN_RATE"),
    MEMORY_SWAP_OUT_RATE("MEMORY_SWAP_OUT_RATE"),
    MEMORY_COMPRESSION_RATE("MEMORY_COMPRESSION_RATE"),
    MEMORY_DECOMPRESSION_RATE("MEMORY_DECOMPRESSION_RATE"),
    NETWORK_PACKETS_RECEIVED_DROPPED("NETWORK_PACKETS_RECEIVED_DROPPED"),
    NETWORK_PACKETS_TRANSMITTED_DROPPED("NETWORK_PACKETS_TRANSMITTED_DROPPED"),
    CRASH_RATE("CRASH_RATE"),
    PAGE_FAULTS("PAGE_FAULTS"),
    COMMAND_ABORT("COMMAND_ABORT"),
    HYPERVISOR_PACKETS_RECEIVED_DROPPED("HYPERVISOR_PACKETS_RECEIVED_DROPPED"),
    HYPERVISOR_PACKETS_TRANSMITTED_DROPPED("HYPERVISOR_PACKETS_TRANSMITTED_DROPPED"),
    PG_AVAILABLE("PG_AVAILABLE"),
    FAILURE_RATE("FAILURE_RATE"),
    RESPONSE_TIME_50TH_PERCENTILE("RESPONSE_TIME_50TH_PERCENTILE"),
    RESPONSE_TIME_90TH_PERCENTILE("RESPONSE_TIME_90TH_PERCENTILE");

    private final String value;

    ContextEnum(String value) {
        this.value = value;
    }

    public static ContextEnum fromValue(String text) {
        return Arrays.stream(ContextEnum.values()).filter(b -> b.value.equals(text)).findFirst().orElse(null);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static class Adapter extends TypeAdapter<ContextEnum> {
        @Override
        public void write(final JsonWriter jsonWriter, final ContextEnum enumeration) throws IOException {
            jsonWriter.value(enumeration.getValue());
        }

        @Override
        public ContextEnum read(final JsonReader jsonReader) throws IOException {
            String value = jsonReader.nextString();
            return ContextEnum.fromValue(value);
        }
    }
}
