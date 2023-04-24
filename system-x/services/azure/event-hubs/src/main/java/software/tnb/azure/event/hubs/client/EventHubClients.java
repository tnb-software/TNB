package software.tnb.azure.event.hubs.client;

import software.tnb.azure.event.hubs.account.EventHubsAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubConsumerClient;
import com.azure.messaging.eventhubs.EventHubProducerClient;

public class EventHubClients {
    private static final Logger LOG = LoggerFactory.getLogger(EventHubClients.class);

    private final EventHubConsumerClient consumerClient;
    private final EventHubProducerClient producerClient;

    public EventHubClients(EventHubsAccount account) {
        LOG.debug("Creating new Azure Event Hub clients");
        producerClient = new EventHubClientBuilder()
            .connectionString(String.format(("Endpoint=sb://%s.servicebus.windows.net/;SharedAccessKeyName=%s;SharedAccessKey=%s;EntityPath=%s"),
                account.getEventHubsNamespace(), account.getEventHubSharedAccessName(), account.getEventHubSharedAccessKey(),
                account.getEvenHubName()))
            .buildProducerClient();
        consumerClient = new EventHubClientBuilder()
            .consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
            .connectionString(String.format(("Endpoint=sb://%s.servicebus.windows.net/;SharedAccessKeyName=%s;SharedAccessKey=%s;EntityPath=%s"),
                account.getEventHubsNamespace(), account.getEventHubSharedAccessName(), account.getEventHubSharedAccessKey(),
                account.getEvenHubName()))
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
