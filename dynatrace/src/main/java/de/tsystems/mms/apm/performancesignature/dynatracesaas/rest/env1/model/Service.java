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

/*
 * Dynatrace Environment API
 * Documentation of the Dynatrace REST API. Refer to the [help page](https://www.dynatrace.com/support/help/shortlink/section-api) to read about use-cases and examples.
 *
 * OpenAPI spec version: 1.0
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env1.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.TagInfo;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils.toIndentedString;

/**
 * Service
 */

public class Service {
    @SerializedName("entityId")
    private String entityId;

    @SerializedName("displayName")
    private String displayName;

    @SerializedName("customizedName")
    private String customizedName;

    @SerializedName("discoveredName")
    private String discoveredName;

    @SerializedName("firstSeenTimestamp")
    private Long firstSeenTimestamp;

    @SerializedName("lastSeenTimestamp")
    private Long lastSeenTimestamp;

    @SerializedName("tags")
    private List<TagInfo> tags;

    @SerializedName("fromRelationships")
    private ServiceFromRelationships fromRelationships;

    @SerializedName("toRelationships")
    private ServiceToRelationships toRelationships;

    @SerializedName("databaseHostNames")
    private List<String> databaseHostNames;

    @SerializedName("port")
    private Integer port;

    @SerializedName("serviceTechnologyTypes")
    private List<String> serviceTechnologyTypes;

    @SerializedName("path")
    private String path;

    @SerializedName("webApplicationId")
    private String webApplicationId;

    @SerializedName("contextRoot")
    private String contextRoot;

    @SerializedName("webServerName")
    private String webServerName;

    @SerializedName("webServiceNamespace")
    private String webServiceNamespace;
    @SerializedName("serviceType")
    private ServiceTypeEnum serviceType;
    @SerializedName("databaseName")
    private String databaseName;
    @SerializedName("ipAddresses")
    private List<String> ipAddresses;
    @SerializedName("softwareTechnologies")
    private List<TechnologyInfo> softwareTechnologies;
    @SerializedName("className")
    private String className;
    @SerializedName("agentTechnologyType")
    private AgentTechnologyTypeEnum agentTechnologyType;
    @SerializedName("webServiceName")
    private String webServiceName;
    @SerializedName("databaseVendor")
    private String databaseVendor;

    public Service entityId(String entityId) {
        this.entityId = entityId;
        return this;
    }

    /**
     * Dynatrace entity ID of the required entity.   You can find them in the URL of the corresponding Dynatrace page, for example, &#x60;HOST-007&#x60;.
     *
     * @return entityId
     **/
    @ApiModelProperty(value = "Dynatrace entity ID of the required entity.   You can find them in the URL of the corresponding Dynatrace page, for example, `HOST-007`.")
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Service displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * The name of the Dynatrace entity, displayed in the UI.
     *
     * @return displayName
     **/
    @ApiModelProperty(value = "The name of the Dynatrace entity, displayed in the UI.")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Service customizedName(String customizedName) {
        this.customizedName = customizedName;
        return this;
    }

    /**
     * Customized name of the entity
     *
     * @return customizedName
     **/
    @ApiModelProperty(value = "Customized name of the entity")
    public String getCustomizedName() {
        return customizedName;
    }

    public void setCustomizedName(String customizedName) {
        this.customizedName = customizedName;
    }

    public Service discoveredName(String discoveredName) {
        this.discoveredName = discoveredName;
        return this;
    }

    /**
     * Discovered name of the entity
     *
     * @return discoveredName
     **/
    @ApiModelProperty(value = "Discovered name of the entity")
    public String getDiscoveredName() {
        return discoveredName;
    }

    public void setDiscoveredName(String discoveredName) {
        this.discoveredName = discoveredName;
    }

    public Service firstSeenTimestamp(Long firstSeenTimestamp) {
        this.firstSeenTimestamp = firstSeenTimestamp;
        return this;
    }

    /**
     * Timestamp in UTC milliseconds when the entity was detected for the first time.
     *
     * @return firstSeenTimestamp
     **/
    @ApiModelProperty(value = "Timestamp in UTC milliseconds when the entity was detected for the first time.")
    public Long getFirstSeenTimestamp() {
        return firstSeenTimestamp;
    }

    public void setFirstSeenTimestamp(Long firstSeenTimestamp) {
        this.firstSeenTimestamp = firstSeenTimestamp;
    }

    public Service lastSeenTimestamp(Long lastSeenTimestamp) {
        this.lastSeenTimestamp = lastSeenTimestamp;
        return this;
    }

    /**
     * Timestamp in UTC milliseconds when the entity was detected for the last time.
     *
     * @return lastSeenTimestamp
     **/
    @ApiModelProperty(value = "Timestamp in UTC milliseconds when the entity was detected for the last time.")
    public Long getLastSeenTimestamp() {
        return lastSeenTimestamp;
    }

    public void setLastSeenTimestamp(Long lastSeenTimestamp) {
        this.lastSeenTimestamp = lastSeenTimestamp;
    }

    public Service tags(List<TagInfo> tags) {
        this.tags = tags;
        return this;
    }

    public Service addTagsItem(TagInfo tagsItem) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        this.tags.add(tagsItem);
        return this;
    }

    /**
     * The list of entity tags.
     *
     * @return tags
     **/
    @ApiModelProperty(value = "The list of entity tags.")
    public List<TagInfo> getTags() {
        return tags;
    }

    public void setTags(List<TagInfo> tags) {
        this.tags = tags;
    }

    public Service fromRelationships(ServiceFromRelationships fromRelationships) {
        this.fromRelationships = fromRelationships;
        return this;
    }

    /**
     * Get fromRelationships
     *
     * @return fromRelationships
     **/
    public ServiceFromRelationships getFromRelationships() {
        return fromRelationships;
    }

    public void setFromRelationships(ServiceFromRelationships fromRelationships) {
        this.fromRelationships = fromRelationships;
    }

    public Service toRelationships(ServiceToRelationships toRelationships) {
        this.toRelationships = toRelationships;
        return this;
    }

    /**
     * Get toRelationships
     *
     * @return toRelationships
     **/
    public ServiceToRelationships getToRelationships() {
        return toRelationships;
    }

    public void setToRelationships(ServiceToRelationships toRelationships) {
        this.toRelationships = toRelationships;
    }

    public Service databaseHostNames(List<String> databaseHostNames) {
        this.databaseHostNames = databaseHostNames;
        return this;
    }

    public Service addDatabaseHostNamesItem(String databaseHostNamesItem) {
        if (this.databaseHostNames == null) {
            this.databaseHostNames = new ArrayList<>();
        }
        this.databaseHostNames.add(databaseHostNamesItem);
        return this;
    }

    /**
     * @return databaseHostNames
     **/
    public List<String> getDatabaseHostNames() {
        return databaseHostNames;
    }

    public void setDatabaseHostNames(List<String> databaseHostNames) {
        this.databaseHostNames = databaseHostNames;
    }

    public Service port(Integer port) {
        this.port = port;
        return this;
    }

    /**
     * @return port
     **/
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Service serviceTechnologyTypes(List<String> serviceTechnologyTypes) {
        this.serviceTechnologyTypes = serviceTechnologyTypes;
        return this;
    }

    public Service addServiceTechnologyTypesItem(String serviceTechnologyTypesItem) {
        if (this.serviceTechnologyTypes == null) {
            this.serviceTechnologyTypes = new ArrayList<>();
        }
        this.serviceTechnologyTypes.add(serviceTechnologyTypesItem);
        return this;
    }

    /**
     * @return serviceTechnologyTypes
     **/
    public List<String> getServiceTechnologyTypes() {
        return serviceTechnologyTypes;
    }

    public void setServiceTechnologyTypes(List<String> serviceTechnologyTypes) {
        this.serviceTechnologyTypes = serviceTechnologyTypes;
    }

    public Service path(String path) {
        this.path = path;
        return this;
    }

    /**
     * @return path
     **/
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Service webApplicationId(String webApplicationId) {
        this.webApplicationId = webApplicationId;
        return this;
    }

    /**
     * @return webApplicationId
     **/
    public String getWebApplicationId() {
        return webApplicationId;
    }

    public void setWebApplicationId(String webApplicationId) {
        this.webApplicationId = webApplicationId;
    }

    public Service contextRoot(String contextRoot) {
        this.contextRoot = contextRoot;
        return this;
    }

    /**
     * @return contextRoot
     **/
    public String getContextRoot() {
        return contextRoot;
    }

    public void setContextRoot(String contextRoot) {
        this.contextRoot = contextRoot;
    }

    public Service webServerName(String webServerName) {
        this.webServerName = webServerName;
        return this;
    }

    /**
     * @return webServerName
     **/
    public String getWebServerName() {
        return webServerName;
    }

    public void setWebServerName(String webServerName) {
        this.webServerName = webServerName;
    }

    public Service webServiceNamespace(String webServiceNamespace) {
        this.webServiceNamespace = webServiceNamespace;
        return this;
    }

    /**
     * @return webServiceNamespace
     **/
    public String getWebServiceNamespace() {
        return webServiceNamespace;
    }

    public void setWebServiceNamespace(String webServiceNamespace) {
        this.webServiceNamespace = webServiceNamespace;
    }

    public Service serviceType(ServiceTypeEnum serviceType) {
        this.serviceType = serviceType;
        return this;
    }

    /**
     * @return serviceType
     **/
    public ServiceTypeEnum getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceTypeEnum serviceType) {
        this.serviceType = serviceType;
    }

    public Service databaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    /**
     * @return databaseName
     **/
    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public Service ipAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
        return this;
    }

    public Service addIpAddressesItem(String ipAddressesItem) {
        if (this.ipAddresses == null) {
            this.ipAddresses = new ArrayList<>();
        }
        this.ipAddresses.add(ipAddressesItem);
        return this;
    }

    /**
     * @return ipAddresses
     **/
    public List<String> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public Service softwareTechnologies(List<TechnologyInfo> softwareTechnologies) {
        this.softwareTechnologies = softwareTechnologies;
        return this;
    }

    public Service addSoftwareTechnologiesItem(TechnologyInfo softwareTechnologiesItem) {
        if (this.softwareTechnologies == null) {
            this.softwareTechnologies = new ArrayList<>();
        }
        this.softwareTechnologies.add(softwareTechnologiesItem);
        return this;
    }

    /**
     * @return softwareTechnologies
     **/
    public List<TechnologyInfo> getSoftwareTechnologies() {
        return softwareTechnologies;
    }

    public void setSoftwareTechnologies(List<TechnologyInfo> softwareTechnologies) {
        this.softwareTechnologies = softwareTechnologies;
    }

    public Service className(String className) {
        this.className = className;
        return this;
    }

    /**
     * @return className
     **/
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Service agentTechnologyType(AgentTechnologyTypeEnum agentTechnologyType) {
        this.agentTechnologyType = agentTechnologyType;
        return this;
    }

    /**
     * @return agentTechnologyType
     **/
    public AgentTechnologyTypeEnum getAgentTechnologyType() {
        return agentTechnologyType;
    }

    public void setAgentTechnologyType(AgentTechnologyTypeEnum agentTechnologyType) {
        this.agentTechnologyType = agentTechnologyType;
    }

    public Service webServiceName(String webServiceName) {
        this.webServiceName = webServiceName;
        return this;
    }

    /**
     * @return webServiceName
     **/
    public String getWebServiceName() {
        return webServiceName;
    }

    public void setWebServiceName(String webServiceName) {
        this.webServiceName = webServiceName;
    }

    public Service databaseVendor(String databaseVendor) {
        this.databaseVendor = databaseVendor;
        return this;
    }

    /**
     * @return databaseVendor
     **/
    public String getDatabaseVendor() {
        return databaseVendor;
    }

    public void setDatabaseVendor(String databaseVendor) {
        this.databaseVendor = databaseVendor;
    }

    @Override
    public String toString() {
        return "class Service {\n"
                + "    entityId: " + toIndentedString(entityId) + "\n"
                + "    displayName: " + toIndentedString(displayName) + "\n"
                + "    customizedName: " + toIndentedString(customizedName) + "\n"
                + "    discoveredName: " + toIndentedString(discoveredName) + "\n"
                + "    firstSeenTimestamp: " + toIndentedString(firstSeenTimestamp) + "\n"
                + "    lastSeenTimestamp: " + toIndentedString(lastSeenTimestamp) + "\n"
                + "    tags: " + toIndentedString(tags) + "\n"
                + "    fromRelationships: " + toIndentedString(fromRelationships) + "\n"
                + "    toRelationships: " + toIndentedString(toRelationships) + "\n"
                + "    databaseHostNames: " + toIndentedString(databaseHostNames) + "\n"
                + "    port: " + toIndentedString(port) + "\n"
                + "    serviceTechnologyTypes: " + toIndentedString(serviceTechnologyTypes) + "\n"
                + "    path: " + toIndentedString(path) + "\n"
                + "    webApplicationId: " + toIndentedString(webApplicationId) + "\n"
                + "    contextRoot: " + toIndentedString(contextRoot) + "\n"
                + "    webServerName: " + toIndentedString(webServerName) + "\n"
                + "    webServiceNamespace: " + toIndentedString(webServiceNamespace) + "\n"
                + "    serviceType: " + toIndentedString(serviceType) + "\n"
                + "    databaseName: " + toIndentedString(databaseName) + "\n"
                + "    ipAddresses: " + toIndentedString(ipAddresses) + "\n"
                + "    softwareTechnologies: " + toIndentedString(softwareTechnologies) + "\n"
                + "    className: " + toIndentedString(className) + "\n"
                + "    agentTechnologyType: " + toIndentedString(agentTechnologyType) + "\n"
                + "    webServiceName: " + toIndentedString(webServiceName) + "\n"
                + "    databaseVendor: " + toIndentedString(databaseVendor) + "\n"
                + "}";
    }

    /**
     *
     */
    @JsonAdapter(ServiceTypeEnum.Adapter.class)
    public enum ServiceTypeEnum {
        UNKNOWN("Unknown"),
        WEBREQUEST("WebRequest"),
        WEBSERVICE("WebService"),
        DATABASE("Database"),
        METHOD("Method"),
        WEBSITE("WebSite"),
        MESSAGING("Messaging"),
        MOBILE("Mobile"),
        PROCESS("Process"),
        RMI("Rmi"),
        EXTERNAL("External"),
        QUEUELISTENER("QueueListener"),
        QUEUEINTERACTION("QueueInteraction"),
        REMOTECALL("RemoteCall"),
        SAASVENDOR("SaasVendor"),
        AMP("AMP"),
        CUSTOMAPPLICATION("CustomApplication"),
        CICS("Cics");

        private final String value;

        ServiceTypeEnum(String value) {
            this.value = value;
        }

        public static ServiceTypeEnum fromValue(String text) {
            return Arrays.stream(ServiceTypeEnum.values()).filter(b -> String.valueOf(b.value).equals(text)).findFirst().orElse(null);
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static class Adapter extends TypeAdapter<ServiceTypeEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final ServiceTypeEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public ServiceTypeEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return ServiceTypeEnum.fromValue(String.valueOf(value));
            }
        }
    }

    /**
     *
     */
    @JsonAdapter(AgentTechnologyTypeEnum.Adapter.class)
    public enum AgentTechnologyTypeEnum {
        N_A("N/A"),
        JAVA("JAVA"),
        DOTNET("DOTNET"),
        SDK("SDK"),
        OS("OS"),
        APACHE("APACHE"),
        WSMB("WSMB"),
        Z("Z"),
        NET("NET"),
        IIS("IIS"),
        PHP("PHP"),
        NODEJS("NODEJS"),
        RUBY("RUBY"),
        NGINX("NGINX"),
        LOG_ANALYTICS("LOG_ANALYTICS"),
        VARNISH("VARNISH"),
        PLUGIN("PLUGIN"),
        PROCESS("PROCESS"),
        UPDATER("UPDATER"),
        GO("GO"),
        REMOTE_PLUGIN("REMOTE_PLUGIN");

        private final String value;

        AgentTechnologyTypeEnum(String value) {
            this.value = value;
        }

        public static AgentTechnologyTypeEnum fromValue(String text) {
            return Arrays.stream(AgentTechnologyTypeEnum.values()).filter(b -> String.valueOf(b.value).equals(text)).findFirst().orElse(null);
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static class Adapter extends TypeAdapter<AgentTechnologyTypeEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final AgentTechnologyTypeEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public AgentTechnologyTypeEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return AgentTechnologyTypeEnum.fromValue(String.valueOf(value));
            }
        }
    }

}

