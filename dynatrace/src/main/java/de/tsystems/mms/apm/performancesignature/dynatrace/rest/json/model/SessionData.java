/*
 * Copyright (c) 2014 T-Systems Multimedia Solutions GmbH
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

package de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.BaseReference;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;
import java.util.Arrays;

/**
 * SessionData
 */

public class SessionData extends BaseReference {
    @SerializedName("storedsessiontype")
    private StoredSessiontypeEnum storedsessiontype;
    @SerializedName("sessiontype")
    private SessionTypeEnum sessiontype;
    @SerializedName("systemprofile")
    private String systemprofile;

    /**
     * Stored session type
     *
     * @return storedsessiontype
     **/
    @ApiModelProperty(value = "Stored session type")
    public StoredSessiontypeEnum getStoredsessiontype() {
        return storedsessiontype;
    }

    /**
     * Session type
     *
     * @return sessiontype
     **/
    @ApiModelProperty(value = "Session type")
    public SessionTypeEnum getSessiontype() {
        return sessiontype;
    }

    /**
     * Name of the system profile the session belongs to
     *
     * @return systemprofile
     **/
    @ApiModelProperty(value = "Name of the system profile the session belongs to")
    public String getSystemprofile() {
        return systemprofile;
    }

    @Override
    public String toString() {
        return "class SessionData {\n" +
                "    id: " + PerfSigUIUtils.toIndentedString(super.getId()) + "\n" +
                "    storedsessiontype: " + PerfSigUIUtils.toIndentedString(storedsessiontype) + "\n" +
                "    sessiontype: " + PerfSigUIUtils.toIndentedString(sessiontype) + "\n" +
                "    systemprofile: " + PerfSigUIUtils.toIndentedString(systemprofile) + "\n" +
                "    href: " + PerfSigUIUtils.toIndentedString(super.getHref()) + "\n" +
                "}";
    }

    /**
     * Stored session type
     */
    @JsonAdapter(StoredSessiontypeEnum.Adapter.class)
    public enum StoredSessiontypeEnum {
        PUREPATH("purepath"),
        MEMDUMP_SIMPLE("memdump_simple"),
        MEMDUMP_EXTENDED("memdump_extended"),
        MEMDUMP_SELECTIVE("memdump_selective"),
        THREADDUMP("threaddump"),
        SAMPLING("sampling");

        private final String value;

        StoredSessiontypeEnum(String value) {
            this.value = value;
        }

        public static StoredSessiontypeEnum fromValue(String text) {
            return Arrays.stream(StoredSessiontypeEnum.values()).filter(b -> b.value.equals(text)).findFirst().orElse(null);
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static class Adapter extends TypeAdapter<StoredSessiontypeEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final StoredSessiontypeEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public StoredSessiontypeEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return StoredSessiontypeEnum.fromValue(value);
            }
        }
    }

    /**
     * Session type
     */
    @JsonAdapter(SessionTypeEnum.Adapter.class)
    public enum SessionTypeEnum {
        LIVE("live"),
        SERVER("server"),
        STORED("stored"),
        UNTYPED("untyped");

        private final String value;

        SessionTypeEnum(String value) {
            this.value = value;
        }

        public static SessionTypeEnum fromValue(String text) {
            return Arrays.stream(SessionTypeEnum.values()).filter(b -> b.value.equals(text)).findFirst().orElse(null);
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static class Adapter extends TypeAdapter<SessionTypeEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final SessionTypeEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public SessionTypeEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return SessionTypeEnum.fromValue(value);
            }
        }
    }

}

