package software.tnb.aws.common.client;

import software.tnb.aws.common.account.AWSAccount;
import software.tnb.aws.common.service.AWSService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.regions.Region;

public final class AWSClient {
    private static final Logger LOG = LoggerFactory.getLogger(AWSService.class);

    private AWSClient() {
    }

    public static <T extends SdkClient> T createDefaultClient(AWSAccount account, Class<T> clazz, String url) {
        LOG.debug("Creating new {} instance", clazz.getSimpleName());
        try {
            final AwsClientBuilder builder = (AwsClientBuilder) clazz.getMethod("builder").invoke(null);
            builder.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(account.accessKey(), account.secretKey())));
            builder.region(Region.of(account.region()));
            if (url != null) {
                builder.endpointOverride(new URI(url));
            }
            return clazz.cast(builder.build());
        } catch (Exception e) {
            throw new RuntimeException("Unable to create " + clazz.getSimpleName() + " instance", e);
        }
    }
}
