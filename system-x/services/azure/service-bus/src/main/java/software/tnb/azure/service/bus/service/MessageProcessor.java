package software.tnb.azure.service.bus.service;

import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class MessageProcessor implements Consumer<ServiceBusReceivedMessageContext> {

    private final ConcurrentLinkedQueue<ServiceBusReceivedMessage> messages;

    public MessageProcessor() {
        this.messages = new ConcurrentLinkedQueue<>();
    }

    public void reset() {
        messages.clear();
    }

    @Override
    public void accept(ServiceBusReceivedMessageContext context) {
        messages.add(context.getMessage());
    }

    public ConcurrentLinkedQueue<ServiceBusReceivedMessage> getMessages() {
        return messages;
    }
}
