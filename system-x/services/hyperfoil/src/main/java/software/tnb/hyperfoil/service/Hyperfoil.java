package software.tnb.hyperfoil.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.service.ConfigurableService;
import software.tnb.hyperfoil.service.configuration.HyperfoilDeploymentConfiguration;
import software.tnb.hyperfoil.validation.HyperfoilValidation;

public abstract class Hyperfoil extends ConfigurableService<NoAccount, NoClient, HyperfoilValidation, HyperfoilDeploymentConfiguration> {
    public abstract String connection();

    public abstract String hyperfoilUrl();

    public HyperfoilValidation validation() {
        return new HyperfoilValidation(connection());
    }

    public abstract int getPortMapping(int port);

    @Override
    protected void defaultConfiguration() {
        getConfiguration().deploymentYamlUrl(HyperfoilDeploymentConfiguration.DEFAULT_YAML_URL);
    }
}
