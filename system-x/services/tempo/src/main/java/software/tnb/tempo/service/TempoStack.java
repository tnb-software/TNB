package software.tnb.tempo.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.service.ConfigurableService;
import software.tnb.common.validation.NoValidation;
import software.tnb.tempo.service.configuration.TempoStackConfiguration;

public abstract class TempoStack extends ConfigurableService<NoAccount, NoClient, NoValidation, TempoStackConfiguration> {

    public abstract String getLog();

    public abstract String getDistributorHostname();

    public abstract String getGatewayHostname();

    @Override
    protected void defaultConfiguration() {
        getConfiguration()
            .withResourceLimitsCpu("2000m")
            .withResourceLimitsMemory("2Gi")
            .withStorageType("s3")
            .withStorageSize("1Gi");
    }
}
