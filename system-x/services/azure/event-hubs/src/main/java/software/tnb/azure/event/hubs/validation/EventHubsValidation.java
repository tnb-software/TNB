package software.tnb.azure.event.hubs.validation;

import com.azure.core.util.IterableStream;
import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubConsumerClient;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.azure.messaging.eventhubs.models.PartitionEvent;
import com.azure.messaging.eventhubs.models.SendOptions;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EventHubsValidation {

    private EventHubProducerClient producerClient;
    private EventHubConsumerClient consumerClient;

    public EventHubsValidation(EventHubConsumerClient consumerClient, EventHubProducerClient producerClient) {
        this.consumerClient = consumerClient;
        this.producerClient = producerClient;
    }

    public void produceEvent(String eventMessage, int partitionId) {
        final SendOptions sendOptions = new SendOptions().setPartitionId(String.valueOf(partitionId));
        producerClient.send(Collections.singletonList(new EventData(eventMessage)), sendOptions);
    }

    public List<String> consumeEvent(int partitionId, int duration, int maxMessages) {
        IterableStream<PartitionEvent> events = consumerClient.receiveFromPartition(String.valueOf(partitionId), maxMessages,
            EventPosition.earliest(), Duration.ofSeconds(duration));
        return events.stream().map(event -> event.getData().getBodyAsString()).collect(Collectors.toList());
    }
}
