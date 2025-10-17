package software.tnb.observability.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.service.ConfigurableService;
import software.tnb.common.validation.NoValidation;
import software.tnb.observability.service.configuration.ObservabilityConfiguration;

public abstract class Observability extends ConfigurableService<NoAccount, NoClient, NoValidation, ObservabilityConfiguration> {

    @Override
    protected void defaultConfiguration() {
        getConfiguration().withDeployTracesUiPlugin(true)
            .withTempoStackName("tnb-tempostack");
    }
}
