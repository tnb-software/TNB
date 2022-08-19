package software.tnb.mllp.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;

import java.util.Map;

public abstract class MllpServer implements Service, WithDockerImage {

    public static final int LISTENING_PORT = 8080;

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/mllp-test-server:latest";
    }

    public abstract String host();

    public abstract int port();

    public abstract String getLog();

    public Map<String, String> containerEnvironment() {
        return Map.of();
    }
}
