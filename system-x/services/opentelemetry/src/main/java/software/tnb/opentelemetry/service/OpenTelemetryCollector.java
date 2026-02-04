package software.tnb.opentelemetry.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithLogs;
import software.tnb.common.service.ConfigurableService;
import software.tnb.common.service.ServiceFactory;
import software.tnb.common.validation.NoValidation;
import software.tnb.opentelemetry.service.configuration.OpenTelemetryCollectorConfiguration;
import software.tnb.tempo.service.Tempo;
import software.tnb.tempo.validation.TempoValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

public abstract class OpenTelemetryCollector extends ConfigurableService<NoAccount, NoClient, NoValidation, OpenTelemetryCollectorConfiguration>
    implements WithLogs {
    private final Tempo tempo = ServiceFactory.create(Tempo.class);

    public abstract String getGrpcEndpoint();

    public abstract String getHttpEndpoint();

    protected void runTempoIfNeeded(ExtensionContext context) throws Exception {
        // Only start Tempo if using Tempo
        if (getConfiguration().isTempo()) {
            tempo.beforeAll(context);
            getConfiguration().withOtlpTracesExporter(tempo.getGatewayUrl());
        }
    }

    protected void cleanTempoIfNeeded(ExtensionContext context) throws Exception {
        // Only clean Tempo if using Tempo
        if (getConfiguration().isTempo()) {
            tempo.afterAll(context);
        }
    }

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

    public TempoValidation tempoValidation() {
        return tempo.validation();
    }
}
