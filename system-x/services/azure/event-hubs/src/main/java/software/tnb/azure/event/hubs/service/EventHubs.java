package software.tnb.azure.event.hubs.service;

import software.tnb.azure.event.hubs.account.EventHubsAccount;
import software.tnb.azure.event.hubs.validation.EventHubsValidation;
import software.tnb.azure.storage.blob.service.StorageBlob;
import software.tnb.common.account.Accounts;
import software.tnb.common.service.Service;
import software.tnb.common.service.ServiceFactory;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubConsumerClient;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.google.auto.service.AutoService;

import java.util.Date;

@AutoService(EventHubs.class)
public class EventHubs implements Service {

    private final StorageBlob storageBlob = ServiceFactory.create(StorageBlob.class);
    private String blobContainer;

    private static final Logger LOG = LoggerFactory.getLogger(EventHubs.class);

    private EventHubsAccount account;
    private EventHubsValidation validation;

    private EventHubConsumerClient consumerClient;
    private EventHubProducerClient producerClient;

    public String getBlobContainer() {
        return blobContainer;
    }

    public EventHubsAccount account() {
        if (account == null) {
            account = Accounts.get(EventHubsAccount.class);
        }
        return account;
    }

    protected EventHubProducerClient producerClient() {
        LOG.debug("Creating new Azure Event Hub producer client");
        return new EventHubClientBuilder()
            .connectionString(String.format(("Endpoint=sb://%s.servicebus.windows.net/;SharedAccessKeyName=%s;SharedAccessKey=%s;EntityPath=%s"),
                account().getEventHubsNamespace(), account().getEventHubSharedAccessName(), account().getEventHubSharedAccessKey(),
                account().getEvenHubName()))
            .buildProducerClient();
    }

    protected EventHubConsumerClient consumerClient() {
        LOG.debug("Creating new Azure Event Hub consumer client");
        return new EventHubClientBuilder()
            .consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
            .connectionString(String.format(("Endpoint=sb://%s.servicebus.windows.net/;SharedAccessKeyName=%s;SharedAccessKey=%s;EntityPath=%s"),
                account().getEventHubsNamespace(), account().getEventHubSharedAccessName(), account().getEventHubSharedAccessKey(),
                account().getEvenHubName()))
            .buildConsumerClient();
    }

    public EventHubsValidation validation() {
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        storageBlob.validation().deleteBlobContainer(blobContainer);
        storageBlob.afterAll(extensionContext);

        producerClient.close();
        consumerClient.close();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        storageBlob.beforeAll(extensionContext);
        blobContainer = "event-hub-blob-" + new Date().getTime();
        LOG.debug("Creating new Azure Storage Blob validation");
        storageBlob.validation().createBlobContainer(blobContainer);

        producerClient = producerClient();
        consumerClient = consumerClient();

        validation = new EventHubsValidation(consumerClient, producerClient);
    }
}
