package software.tnb.aws.kinesis.validation;

import software.tnb.common.service.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.GetRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.Record;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;

public class KinesisValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(KinesisValidation.class);

    private final KinesisClient client;

    public KinesisValidation(KinesisClient client) {
        this.client = client;
    }

    public void createDataStream(String name) {
        LOG.debug("Creating Kinesis data stream {}", name);
        client.createStream(b -> b.streamName(name).shardCount(1));
    }

    public void waitForDataStream(String name) {
        LOG.debug("Waiting for Kinesis data stream {} to be ready", name);
        client.waiter().waitUntilStreamExists(builder -> builder.streamName(name));
        LOG.debug("Kinesis data stream {} is ready!", name);
    }

    public void deleteDataStream(String name) {
        LOG.debug("Deleting Kinesis data stream {}", name);
        client.deleteStream(b -> b.streamName(name));
    }

    public void sendMessage(String stream, String message, String partitionKey) {
        client.putRecord(b -> b.streamName(stream).partitionKey(partitionKey).data(SdkBytes.fromUtf8String(message)));
    }

    public List<Record> getRecords(String streamName) {
        List<Shard> initialShardData = client.describeStream(b -> b.streamName(streamName)).streamDescription().shards();

        List<String> initialShardIterators = initialShardData.stream().map(s ->
            client.getShardIterator(builder -> builder.shardId(s.shardId()).streamName(streamName)
                .startingSequenceNumber(s.sequenceNumberRange().startingSequenceNumber())
                .shardIteratorType(ShardIteratorType.AT_SEQUENCE_NUMBER)
            ).shardIterator()
        ).collect(Collectors.toList());
        // The stream has only one shard, so use only that shard
        String shardIterator = initialShardIterators.get(0);
        GetRecordsResponse recordResult = client.getRecords(b -> b.shardIterator(shardIterator).limit(25));

        return recordResult.records();
    }
}
