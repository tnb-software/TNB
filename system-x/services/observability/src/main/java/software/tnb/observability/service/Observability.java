package software.tnb.observability.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.service.ConfigurableService;
import software.tnb.observability.service.configuration.ObservabilityConfiguration;
import software.tnb.observability.validation.ObservabilityValidation;

public abstract class Observability extends ConfigurableService<NoAccount, NoClient, ObservabilityValidation, ObservabilityConfiguration> {

    @Override
    protected void defaultConfiguration() {
        getConfiguration().withDeployTracesUiPlugin(true)
            .withTempoStackName("tnb-tempostack");
    }
}
