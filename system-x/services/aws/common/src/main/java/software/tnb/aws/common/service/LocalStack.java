package software.tnb.aws.common.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;

public abstract class LocalStack implements Service, WithDockerImage {
    protected static final int PORT = 4566;

    public abstract String serviceUrl();

    public abstract String clientUrl();

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/localstack:1.4.0";
    }
}
