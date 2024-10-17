package software.tnb.aws.kinesis.validation;

import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.GetRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;

public class KinesisValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(KinesisValidation.class);

    private final KinesisClient client;

    public KinesisValidation(KinesisClient client) {
        this.client = client;
    }

    public void createDataStream(String name) {
        LOG.info("Creating Kinesis data stream {}", name);
        client.createStream(b -> b.streamName(name).shardCount(1));
        waitForDataStream(name);
    }

    private void waitForDataStream(String name) {
        LOG.debug("Waiting for Kinesis data stream {} to be ready", name);
        client.waiter().waitUntilStreamExists(builder -> builder.streamName(name));
        LOG.debug("Kinesis data stream {} is ready", name);
    }

    public void deleteDataStream(String name) {
        LOG.info("Deleting Kinesis data stream {}", name);
        client.deleteStream(b -> b.streamName(name));
    }

    public void sendMessage(String stream, String message, String partitionKey) {
        client.putRecord(b -> b.streamName(stream).partitionKey(partitionKey).data(SdkBytes.fromUtf8String(message)));
    }

    public GetRecordsResponse getRecords(String streamName) {
        return getRecords(streamName, 25);
    }

    public GetRecordsResponse getRecords(String streamName, int maxNumberOfRecords) {
        List<Shard> initialShardData = client.describeStream(b -> b.streamName(streamName)).streamDescription().shards();

        List<String> initialShardIterators = initialShardData.stream().map(s ->
            client.getShardIterator(builder -> builder.shardId(s.shardId()).streamName(streamName)
                .startingSequenceNumber(s.sequenceNumberRange().startingSequenceNumber())
                .shardIteratorType(ShardIteratorType.AT_SEQUENCE_NUMBER)
            ).shardIterator()
        ).toList();
        // The stream has only one shard, so use only that shard
        return getRecords(maxNumberOfRecords, initialShardIterators.get(0));
    }

    public GetRecordsResponse getRecords(int maxNumberOfRecords, String shardIterator) {
        return client.getRecords(b -> b.shardIterator(shardIterator).limit(maxNumberOfRecords));
    }
}
