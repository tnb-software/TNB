package software.tnb.flink.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class FlinkConfiguration extends ServiceConfiguration {

    private static final String FORCE_USE_IMAGE_SERVER = "flink.use.image.server";

    public FlinkConfiguration forceUseImageServer(boolean value) {
        set(FORCE_USE_IMAGE_SERVER, value);
        return this;
    }

    public boolean isImageServerForced() {
        return get(FORCE_USE_IMAGE_SERVER, Boolean.class);
    }
}

