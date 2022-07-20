package software.tnb.lracoordinator.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class LRACoordinator implements Service, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(LRACoordinator.class);

    public static final int DEFAULT_PORT = 8888;

    public Map<String, String> containerEnvironment() {
        return Map.of("QUARKUS_HTTP_PORT", String.valueOf(DEFAULT_PORT)
            , "LOG_LEVEL", "DEBUG"
        );
    }

    public abstract String hostname();

    public abstract int port();

    public String defaultImage() {
        return "quay.io/rh_integration/lra-coordinator:5.12.4.Final";
    }

    public void openResources() {
    }

    public void closeResources() {
    }

    public abstract String getUrl();

    public abstract String getLog();
}
