package software.tnb.flink.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.ConfigurableService;
import software.tnb.common.validation.NoValidation;
import software.tnb.flink.service.configuration.FlinkConfiguration;

public abstract class Flink<A extends NoAccount, C extends NoClient, V extends NoValidation>
    extends ConfigurableService<A, C, V, FlinkConfiguration> implements WithDockerImage {

    protected static final int PORT = 8081;

    public abstract String host();

    public abstract int port();

    public String defaultImage() {
        return "quay.io/fuse_qe/flink:java17";
    }

    @Override
    protected void defaultConfiguration() {
        getConfiguration().forceUseImageServer(false);
    }

    protected boolean getCurrentForcingConfiguration() {
        return getConfiguration().isImageServerForced();
    }

}
