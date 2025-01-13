package software.tnb.aws.s3.service;

import software.tnb.aws.common.account.AWSAccount;
import software.tnb.aws.common.service.AWSService;
import software.tnb.aws.s3.validation.S3Validation;
import software.tnb.common.deployment.WithDockerImage;

import java.net.URI;
import java.util.Map;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public abstract class Ceph extends AWSService<AWSAccount, S3Client, S3Validation> implements WithDockerImage {
    protected static final int CONTAINER_PORT = 80;

    public abstract String hostname();

    public abstract String clientHostname();

    @Override
    public AWSAccount account() {
        if (account == null) {
            LOG.debug("Creating new Ceph account");
            account = new AWSAccount();
            account.setAccess_key("ceph");
            account.setSecret_key("ceph123ceph123ceph123");
            // this needs to be us-east-1 otherwise the bucket creation does not work
            account.setRegion("us-east-1");
        }
        return account;
    }

    @Override
    protected S3Client client() {
        if (client == null) {
            client = S3Client.builder()
                .endpointOverride(URI.create(clientHostname()))
                .region(Region.of(account().region()))
                .credentialsProvider(() -> AwsBasicCredentials.create(account().accessKey(), account().secretKey()))
                .serviceConfiguration(conf -> conf.pathStyleAccessEnabled(true))
                .build();
        }
        return client;
    }

    @Override
    public String defaultImage() {
        // squid == ceph 19 - https://docs.ceph.com/en/latest/releases/
        return "quay.io/ceph/demo:main-30dc8b9a-squid-centos-stream9";
    }

    protected Map<String, String> environment() {
        return Map.of(
            "MON_IP", "127.0.0.1",
            "CEPH_DEMO_ACCESS_KEY", account().accessKey(),
            "CEPH_DEMO_SECRET_KEY", account().secretKey(),
            "CEPH_DEMO_UID", "demo",
            "RGW_NAME", "localhost",
            "RGW_FRONTEND_PORT", CONTAINER_PORT + "",
            "CEPH_PUBLIC_NETWORK", "0.0.0.0/0"
        );
    }
}
