package software.tnb.azure.event.hubs.validation;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubConsumerClient;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.azure.messaging.eventhubs.models.PartitionEvent;
import com.azure.messaging.eventhubs.models.SendOptions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EventHubsValidation {
    private final EventHubProducerClient producerClient;
    private final EventHubConsumerClient consumerClient;

    public EventHubsValidation(EventHubConsumerClient consumerClient, EventHubProducerClient producerClient) {
        this.consumerClient = consumerClient;
        this.producerClient = producerClient;
    }

    public void produceEvent(String message) {
        produceEvent(message, "0");
    }

    public void produceEvent(String message, String partitionId) {
        final SendOptions sendOptions = new SendOptions().setPartitionId(partitionId);
        producerClient.send(Collections.singletonList(new EventData(message)), sendOptions);
    }

    /**
     * This method loops through all partitions in the eventhub and for each partition it waits for the maxWaitTime / maxEvents.
     *
     * @param maxWaitTime maximum wait for new events
     * @param maxEvents maximum events to receive
     * @return list of events from all partitions
     */
    public List<PartitionEvent> consumeEvents(Duration maxWaitTime, int maxEvents) {
        List<PartitionEvent> messages = new ArrayList<>();
        for (String partitionId : consumerClient.getPartitionIds()) {
            messages.addAll(consumeEvents(partitionId, maxWaitTime, maxEvents));
        }
        return messages;
    }

    public List<PartitionEvent> consumeEvents(String partitionId, Duration duration, int maxEvents) {
        return consumerClient.receiveFromPartition(partitionId, maxEvents, EventPosition.earliest(), duration).stream()
            .collect(Collectors.toList());
    }
}
