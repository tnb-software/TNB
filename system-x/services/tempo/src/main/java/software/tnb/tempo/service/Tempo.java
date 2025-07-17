package software.tnb.tempo.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.service.ConfigurableService;
import software.tnb.common.validation.NoValidation;
import software.tnb.tempo.service.configuration.TempoConfiguration;

public abstract class Tempo extends ConfigurableService<NoAccount, NoClient, NoValidation, TempoConfiguration> {

    public abstract String getLog();

    public abstract String getDistributorHostname();

    public abstract String getGatewayHostname();

    public abstract String getGatewayUrl();

    @Override
    protected void defaultConfiguration() {
        getConfiguration()
            .withResourceLimitsCpu("2000m")
            .withResourceLimitsMemory("2Gi");
    }
}
