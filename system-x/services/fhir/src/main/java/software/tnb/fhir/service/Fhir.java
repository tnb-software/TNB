package software.tnb.fhir.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.ConfigurableService;
import software.tnb.common.validation.NoValidation;
import software.tnb.fhir.service.configuration.FhirConfiguration;

import java.util.Map;

public abstract class Fhir extends ConfigurableService<NoAccount, NoClient, NoValidation, FhirConfiguration> implements WithDockerImage {
    public static final int PORT = 8080;

    public abstract int getPortMapping();

    public abstract String getServerUrl();

    public Map<String, String> containerEnvironment() {
        return Map.of(
            "hapi.fhir.fhir_version", getConfiguration().fhirVersion(),
            "hapi.fhir.reuse_cached_search_results_millis", "-1");
    }

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/hapi:v7.2.0";
    }

    @Override
    protected void defaultConfiguration() {
        getConfiguration().withFhirVersion("R4");
    }
}
