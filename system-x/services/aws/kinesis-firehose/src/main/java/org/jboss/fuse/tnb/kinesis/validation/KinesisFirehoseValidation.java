package org.jboss.fuse.tnb.kinesis.validation;

import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.kinesis.account.KinesisFirehoseAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.firehose.model.DeliveryStreamStatus;

public class KinesisFirehoseValidation {

    private static final Logger LOG = LoggerFactory.getLogger(KinesisFirehoseValidation.class);

    private final FirehoseClient client;
    private final KinesisFirehoseAccount account;

    public KinesisFirehoseValidation(FirehoseClient client, KinesisFirehoseAccount account) {
        this.client = client;
        this.account = account;
    }

    public void createStream(String bucketARN, String streamName) {
        String arnRole = String.format("arn:aws:iam::%s:role/service-role/%s", account.accountId(), account.kinesisRoleArn());
        String kinesisPrefix = "myFirehose/testfile=!{firehose:random-string}";

        LOG.debug("Creating Kinesis firehose stream {}", streamName);

        client.createDeliveryStream(
            b -> b.deliveryStreamName(streamName)
                .extendedS3DestinationConfiguration(
                    builder -> builder.bucketARN(bucketARN).roleARN(arnRole).prefix(kinesisPrefix)
                        .errorOutputPrefix("error")
                        .processingConfiguration(builder1 -> builder1.enabled(false))
                        .dataFormatConversionConfiguration(builder1 -> builder1.enabled(false))
                        .bufferingHints(builder1 -> builder1.intervalInSeconds(60).sizeInMBs(1)))
                .deliveryStreamType("DirectPut")
        );
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
        return client.listDeliveryStreams(builder -> builder.build()).deliveryStreamNames();
    }
}
