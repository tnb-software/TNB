package software.tnb.hyperfoil.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class HyperfoilDeploymentConfiguration extends ServiceConfiguration {

    private static final String DEPLOYMENT_YAML_URL = "deployment.yaml.url";
    public static final String DEFAULT_YAML_URL =
        "https://raw.githubusercontent.com/Hyperfoil/Hyperfoil/refs/heads/0.29.x/k8s-deployer/hyperfoil.yaml";

    public HyperfoilDeploymentConfiguration deploymentYamlUrl(String url) {
        set(DEPLOYMENT_YAML_URL, url);
        return this;
    }

    public String getDeploymentYamlUrl() {
        return get(DEPLOYMENT_YAML_URL, String.class);
    }
}
