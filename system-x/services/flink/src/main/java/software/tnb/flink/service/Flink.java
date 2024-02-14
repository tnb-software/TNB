package software.tnb.flink.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.common.validation.NoValidation;

public abstract class Flink extends Service<NoAccount, NoClient, NoValidation> implements WithDockerImage {

    protected static final int PORT = 8081;

    public abstract String host();

    public abstract int port();

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/flink:java17";
    }
}
