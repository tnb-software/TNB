package software.tnb.azure.service.bus.validation;

import software.tnb.azure.common.account.AzureServiceBusAccount;
import software.tnb.azure.service.bus.service.ErrorProcessor;
import software.tnb.azure.service.bus.service.MessageProcessor;
import software.tnb.common.utils.WaitUtils;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServiceBusValidation {
    private final AzureServiceBusAccount azureServiceBusAccount;
    private final ServiceBusAdministrationClient adminClient;

    public ServiceBusValidation(AzureServiceBusAccount azureServiceBusAccount, ServiceBusAdministrationClient adminClient) {
        this.azureServiceBusAccount = azureServiceBusAccount;
        this.adminClient = adminClient;
    }

    public AzureServiceBusAccount getAzureServiceBusAccount() {
        return azureServiceBusAccount;
    }

    public void createQueue(String queue) {
        adminClient.createQueue(queue);
        WaitUtils.waitFor(() -> adminClient.getQueueExists(queue), 10
            , 1000L, "Waiting until the queue " + queue + " is created");
    }

    public void deleteQueue(String queue) {
        adminClient.deleteQueue(queue);
        WaitUtils.waitFor(() -> !adminClient.getQueueExists(queue), 10
            , 1000L, "Waiting until the queue " + queue + " is deleted");
    }

    public void sendMessage(String queue, String message) {
        ServiceBusSenderClient client = new ServiceBusClientBuilder()
            .connectionString(azureServiceBusAccount.connectionString())
            .sender()
            .queueName(queue)
            .buildClient();

        client.sendMessage(new ServiceBusMessage(message));
    }

    public List<String> receiveMessages(String queue) {
        final MessageProcessor messageProcessor = new MessageProcessor();
        final ErrorProcessor errorProcessor = new ErrorProcessor();

        try (ServiceBusProcessorClient client = new ServiceBusClientBuilder()
            .connectionString(azureServiceBusAccount.connectionString())
            .processor()
            .queueName(queue)
            .processMessage(messageProcessor)
            .processError(errorProcessor)
            .buildProcessorClient()) {

            client.start();
            WaitUtils.waitFor(() -> !(errorProcessor.getErrors().isEmpty()
                    && messageProcessor.getMessages().isEmpty())
                , 10, 1000L, "Waiting for messages");
        }

        return Stream.concat(errorProcessor.getErrors().stream()
                , messageProcessor.getMessages().stream()
                    .map(serviceBusReceivedMessage -> serviceBusReceivedMessage.getBody().toString()))
            .collect(Collectors.toList());
    }
}
