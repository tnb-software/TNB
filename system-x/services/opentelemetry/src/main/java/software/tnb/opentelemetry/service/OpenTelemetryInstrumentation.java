package software.tnb.opentelemetry.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.service.ConfigurableService;
import software.tnb.common.validation.NoValidation;
import software.tnb.opentelemetry.service.configuration.OpenTelemetryInstrumentationConfiguration;

public abstract class OpenTelemetryInstrumentation extends ConfigurableService<NoAccount, NoClient, NoValidation
    , OpenTelemetryInstrumentationConfiguration> {

    @Override
    protected void defaultConfiguration() {
        getConfiguration();
    }
}
