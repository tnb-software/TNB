package software.tnb.azure.event.hubs.validation;

import software.tnb.azure.event.hubs.client.EventHubClients;
import software.tnb.common.validation.Validation;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.azure.messaging.eventhubs.models.PartitionEvent;
import com.azure.messaging.eventhubs.models.SendOptions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EventHubsValidation implements Validation {
    private final EventHubClients clients;

    public EventHubsValidation(EventHubClients clients) {
        this.clients = clients;
    }

    public void produceEvent(String message) {
        produceEvent(message, "0");
    }

    public void produceEvent(String message, String partitionId) {
        final SendOptions sendOptions = new SendOptions().setPartitionId(partitionId);
        clients.producerClient().send(Collections.singletonList(new EventData(message)), sendOptions);
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
        for (String partitionId : clients.consumerClient().getPartitionIds()) {
            messages.addAll(consumeEvents(partitionId, maxWaitTime, maxEvents));
        }
        return messages;
    }

    public List<PartitionEvent> consumeEvents(String partitionId, Duration duration, int maxEvents) {
        return clients.consumerClient().receiveFromPartition(partitionId, maxEvents, EventPosition.earliest(), duration).stream()
            .collect(Collectors.toList());
    }
}
