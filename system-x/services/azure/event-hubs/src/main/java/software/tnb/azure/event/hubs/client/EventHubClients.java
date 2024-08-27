package software.tnb.azure.event.hubs.client;

import software.tnb.azure.event.hubs.account.AzureEventHubsAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubConsumerClient;
import com.azure.messaging.eventhubs.EventHubProducerClient;

public class EventHubClients {
    private static final Logger LOG = LoggerFactory.getLogger(EventHubClients.class);

    private final EventHubConsumerClient consumerClient;
    private final EventHubProducerClient producerClient;

    public EventHubClients(AzureEventHubsAccount account) {
        LOG.debug("Creating new Azure Event Hub clients");
        producerClient = new EventHubClientBuilder().connectionString(account.connectionString(), account.eventHubName()).buildProducerClient();
        consumerClient = new EventHubClientBuilder()
            .consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME).connectionString(account.connectionString())
            .buildConsumerClient();
    }

    public EventHubConsumerClient consumerClient() {
        return consumerClient;
    }

    public EventHubProducerClient producerClient() {
        return producerClient;
    }

    public void close() {
        if (consumerClient != null) {
            consumerClient.close();
        }
        if (producerClient != null) {
            producerClient.close();
        }
    }
}
