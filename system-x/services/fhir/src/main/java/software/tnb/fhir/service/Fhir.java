package software.tnb.fhir.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;

import java.util.Map;

public abstract class Fhir implements Service, WithDockerImage {
    public static final String FHIR_IMAGE = "quay.io/fuse_qe/hapi:v4.2.0";
    public static final int PORT = 8080;

    public abstract int getPortMapping();

    public abstract String getServerUrl();

    public Map<String, String> containerEnvironment() {
        return Map.of(
            "HAPI_FHIR_VERSION", "DSTU3",
            "HAPI_REUSE_CACHED_SEARCH_RESULTS_MILLIS", "-1");
    }
}
