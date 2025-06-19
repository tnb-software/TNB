package software.tnb.fhir.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class FhirConfiguration extends ServiceConfiguration {

    //supported versions https://github.com/hapifhir/hapi-fhir/blob/master/hapi-fhir-base/src/main/java/ca/uhn/fhir/context/FhirVersionEnum.java
    private static final String FHIR_VERSION = "fhir.version";
    private static final String FHIR_AUTH_DISABLED = "DISABLE_AUTH";

    public FhirConfiguration withFhirVersion(String fhirVersion) {
        set(FHIR_VERSION, fhirVersion);
        return this;
    }

    public FhirConfiguration withFhirAuthDisabled(boolean fhirAuthDisabled) {
        set(FHIR_AUTH_DISABLED, fhirAuthDisabled);
        return this;
    }

    public String fhirVersion() {
        return get(FHIR_VERSION, String.class);
    }

    public boolean getFhirAuthDisabled() {
        return get(FHIR_AUTH_DISABLED, Boolean.class);
    }
}
