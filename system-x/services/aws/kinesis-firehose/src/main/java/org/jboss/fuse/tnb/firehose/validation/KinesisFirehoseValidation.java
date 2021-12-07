package org.jboss.fuse.tnb.firehose.validation;

import static org.junit.jupiter.api.Assertions.fail;

import org.jboss.fuse.tnb.common.service.Validation;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.iam.service.IAM;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.firehose.model.DeliveryStreamStatus;
import software.amazon.awssdk.services.firehose.model.InvalidArgumentException;
import software.amazon.awssdk.utils.builder.SdkBuilder;

public class KinesisFirehoseValidation implements Validation {
    private static final String ROLE_NAME = "tnb-kinesis-firehose-role";
    private static final Logger LOG = LoggerFactory.getLogger(KinesisFirehoseValidation.class);

    private final FirehoseClient client;
    private final IAM iam;

    public KinesisFirehoseValidation(FirehoseClient client, IAM iam) {
        this.client = client;
        this.iam = iam;
    }

    public void createStream(String bucketARN, String streamName) {
        String rolePolicy = null;

        try (InputStream is = this.getClass().getResourceAsStream("/role-policy.json")) {
            rolePolicy = IOUtils.toString(is, Charset.defaultCharset());
        } catch (IOException e) {
            fail("Unable to read role-policy.json file", e);
        }

        final String roleArn = iam.validation().createRole(
            ROLE_NAME,
            "Used in TNB Kinesis Firehose service, if deleted, it will be automatically recreated when running the tests again",
            rolePolicy
        );
        iam.validation().attachPolicy(ROLE_NAME, "arn:aws:iam::aws:policy/AmazonS3FullAccess");

        String kinesisPrefix = "myFirehose/testfile=!{firehose:random-string}";
        LOG.debug("Creating Kinesis firehose stream {}", streamName);

        // It takes some time until you can use the role (usually around ~10 seconds)
        int retries = 0;
        while (retries < 12) {
            try {
                client.createDeliveryStream(
                    b -> b.deliveryStreamName(streamName)
                        .extendedS3DestinationConfiguration(
                            builder -> builder.bucketARN(bucketARN).roleARN(roleArn).prefix(kinesisPrefix)
                                .errorOutputPrefix("error")
                                .processingConfiguration(builder1 -> builder1.enabled(false))
                                .dataFormatConversionConfiguration(builder1 -> builder1.enabled(false))
                                .bufferingHints(builder1 -> builder1.intervalInSeconds(60).sizeInMBs(1)))
                        .deliveryStreamType("DirectPut")
                );
                break;
            } catch (InvalidArgumentException ex) {
                if (ex.getMessage().contains("Firehose is unable to assume role")) {
                    LOG.trace("Role not ready yet, will be retried");
                    retries++;
                    WaitUtils.sleep(5000L);
                } else {
                    throw new RuntimeException("Unable to create lambda function: ", ex);
                }
            }
        }

        WaitUtils.waitFor(
            () -> client.describeDeliveryStream(b -> b.deliveryStreamName(streamName).build()).deliveryStreamDescription().deliveryStreamStatus()
                == DeliveryStreamStatus.ACTIVE, "Waiting until the Firehose stream is active");
    }

    public void sendMessage(String stream, String message) {
        client.putRecord(builder -> builder.deliveryStreamName(stream).record(b -> b.data(SdkBytes.fromUtf8String(message))));
    }

    public void deleteDataStream(String name) {
        LOG.debug("Deleting Kinesis Firehose data stream {}", name);
        client.deleteDeliveryStream(builder -> builder.deliveryStreamName(name));
    }

    public List<String> listDeliveryStreams() {
        return client.listDeliveryStreams(SdkBuilder::build).deliveryStreamNames();
    }
}
