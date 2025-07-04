package software.tnb.opentelemetry.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.service.ConfigurableService;
import software.tnb.common.validation.NoValidation;
import software.tnb.opentelemetry.service.configuration.OpenTelemetryCollectorConfiguration;

public abstract class OpenTelemetryCollector extends ConfigurableService<NoAccount, NoClient, NoValidation, OpenTelemetryCollectorConfiguration> {

    public abstract String getLog();

    public abstract String getGrpcEndpoint();

    public abstract String getHttpEndpoint();

    @Override
    protected void defaultConfiguration() {
        getConfiguration().withDefaultReceivers()
            .withDefaultProcessors()
            .withDefaultExporters()
            .withDefaultServices()
            .withReplicas(1)
            .withActuatorHealthCheckExcluded()
            .useTempostack(false);
    }
}
