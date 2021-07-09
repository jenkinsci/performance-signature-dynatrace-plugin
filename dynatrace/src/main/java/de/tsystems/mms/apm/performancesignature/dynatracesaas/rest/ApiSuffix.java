package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest;

import java.util.Arrays;

public enum ApiSuffix {

    ENVIRONMENT("api/v1/"),
    ENVIRONMENT2("api/v2/"),
    CONFIGURATION("api/config/v1/"),
    CLUSTERMANAGEMENT("api/v1.0/onpremise/"),
    CLUSTERAPI("api/cluster/v1/");

    private final String value;

    ApiSuffix(String value) {
        this.value = value;
    }

    public static ApiSuffix fromValue(String text) {
        return Arrays.stream(ApiSuffix.values())
                .filter(b -> b.value.equals(text))
                .findFirst()
                .orElse(null);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
