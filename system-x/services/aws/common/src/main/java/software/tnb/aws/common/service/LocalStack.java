package software.tnb.aws.common.service;

import software.tnb.aws.common.account.AWSAccount;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.common.validation.Validation;

import software.amazon.awssdk.core.SdkClient;

public abstract class LocalStack extends Service<AWSAccount, SdkClient, Validation> implements WithDockerImage {
    protected static final int PORT = 4566;

    public abstract String serviceUrl();

    public abstract String clientUrl();

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/localstack:1.4.0";
    }
}
