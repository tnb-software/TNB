package software.tnb.aws.common.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class AWSConfiguration extends ServiceConfiguration {
    private static final String USE_LOCALSTACK = "aws.use.localstack";

    public AWSConfiguration useLocalstack(boolean value) {
        set(USE_LOCALSTACK, value);
        return this;
    }

    public boolean isLocalstack() {
        return get(USE_LOCALSTACK, Boolean.class);
    }
}
