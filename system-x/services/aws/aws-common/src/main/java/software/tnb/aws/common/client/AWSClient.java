package software.tnb.aws.common.client;

import software.tnb.aws.common.account.AWSAccount;
import software.tnb.aws.common.service.AWSService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.core.SdkClient;

public final class AWSClient {
    private static final Logger LOG = LoggerFactory.getLogger(AWSService.class);

    private AWSClient() {
    }

    public static <T extends SdkClient> T createDefaultClient(AWSAccount account, Class<T> clazz) {
        LOG.debug("Creating new {} instance", clazz.getSimpleName());
        try {
            System.setProperty("aws.accessKeyId", account.accessKey());
            System.setProperty("aws.secretAccessKey", account.secretKey());
            System.setProperty("aws.region", account.region());
            return clazz.cast(clazz.getMethod("create").invoke(null));
        } catch (Exception e) {
            throw new RuntimeException("Unable to create " + clazz.getSimpleName() + " instance", e);
        }
    }
}
