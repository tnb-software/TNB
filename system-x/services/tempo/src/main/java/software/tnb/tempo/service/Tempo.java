package software.tnb.tempo.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.service.ConfigurableService;
import software.tnb.tempo.service.configuration.TempoConfiguration;
import software.tnb.tempo.validation.TempoValidation;

public abstract class Tempo extends ConfigurableService<NoAccount, NoClient, TempoValidation, TempoConfiguration> {

    public abstract String getDistributorHostname();

    public abstract String getGatewayHostname();

    public abstract String getGatewayUrl();

    public abstract String getGatewayExternalUrl();

    @Override
    protected void defaultConfiguration() {
        getConfiguration()
            .withResourceLimitsCpu("2000m")
            .withResourceLimitsMemory("2Gi");
    }
}
