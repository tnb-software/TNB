package software.tnb.aws.s3.service;

import software.tnb.aws.common.account.AWSAccount;
import software.tnb.aws.common.service.AWSService;
import software.tnb.aws.s3.validation.S3Validation;
import software.tnb.common.account.AccountFactory;
import software.tnb.common.deployment.WithDockerImage;

import java.net.URI;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public abstract class Minio extends AWSService<AWSAccount, S3Client, S3Validation> implements WithDockerImage {
    protected static final int CONTAINER_API_PORT = 9000;
    protected static final int CONTAINER_UI_PORT = 9001;

    public abstract String hostname();

    protected abstract String clientHostname();

    @Override
    public AWSAccount account() {
        if (account == null) {
            LOG.debug("Creating new Minio account");
            account = AccountFactory.create(AWSAccount.class);
            account.setAccount_id("minio");
            account.setAccess_key("minio");
            account.setSecret_key("minio123minio123minio123");
        }
        return account;
    }

    @Override
    protected S3Client client(Class<S3Client> clazz) {
        if (client == null) {
            client = S3Client.builder()
                .endpointOverride(URI.create(clientHostname()))
                .region(Region.of(account().region()))
                .credentialsProvider(() -> AwsBasicCredentials.create(account().accountId(), account().secretKey()))
                .forcePathStyle(true)
                .build();
        }
        return client;
    }

    @Override
    public String defaultImage() {
        return "quay.io/minio/minio:RELEASE.2022-09-17T00-09-45Z";
    }
}
