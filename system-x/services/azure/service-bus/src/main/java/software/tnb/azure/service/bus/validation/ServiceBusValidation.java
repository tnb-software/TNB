package software.tnb.azure.service.bus.validation;

import software.tnb.azure.service.bus.account.AzureServiceBusAccount;
import software.tnb.azure.service.bus.service.ErrorProcessor;
import software.tnb.azure.service.bus.service.MessageProcessor;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.validation.Validation;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServiceBusValidation implements Validation {
    private final AzureServiceBusAccount serviceBusAccount;
    private final ServiceBusAdministrationClient adminClient;

    public ServiceBusValidation(AzureServiceBusAccount serviceBusAccount, ServiceBusAdministrationClient adminClient) {
        this.serviceBusAccount = serviceBusAccount;
        this.adminClient = adminClient;
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
        try (ServiceBusSenderClient client = new ServiceBusClientBuilder()
            .connectionString(serviceBusAccount.connectionString())
            .sender()
            .queueName(queue)
            .buildClient()) {
            client.sendMessage(new ServiceBusMessage(message));
        }
    }

    public List<String> receiveMessages(String queue) {
        final MessageProcessor messageProcessor = new MessageProcessor();
        final ErrorProcessor errorProcessor = new ErrorProcessor();

        try (ServiceBusProcessorClient client = new ServiceBusClientBuilder()
            .connectionString(serviceBusAccount.connectionString())
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
