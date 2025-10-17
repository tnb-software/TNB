package software.tnb.fhir.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class FhirConfiguration extends ServiceConfiguration {

    //supported versions https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-base/src/main/java/ca/uhn/fhir/context/FhirVersionEnum.java
    private static final String FHIR_VERSION = "FHIR_VERSION";
    private static final String AUTH_ENABLED = "AUTH_ENABLED";

    public FhirConfiguration withFhirVersion(String fhirVersion) {
        set(FHIR_VERSION, fhirVersion);
        return this;
    }

    public FhirConfiguration withFhirAuthEnabled(boolean fhirAuthEnabled) {
        set(AUTH_ENABLED, fhirAuthEnabled);
        return this;
    }

    public String fhirVersion() {
        return get(FHIR_VERSION, String.class);
    }

    public boolean getFhirAuthEnabled() {
        return get(AUTH_ENABLED, Boolean.class);
    }
}
