package software.tnb.azure.service.bus.service;

import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusException;
import com.azure.messaging.servicebus.ServiceBusFailureReason;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ErrorProcessor implements Consumer<ServiceBusErrorContext> {

    private final List<String> errors;

    public ErrorProcessor() {
        this.errors = new ArrayList<>();
    }

    @Override
    public void accept(ServiceBusErrorContext context) {
        CountDownLatch countdownLatch = new CountDownLatch(1);
        errors.add(String.format("Error when receiving messages from namespace: '%s'. Entity: '%s'%n",
            context.getFullyQualifiedNamespace(), context.getEntityPath()));

        if (!(context.getException() instanceof ServiceBusException)) {
            errors.add(String.format("Non-ServiceBusException occurred: %s%n", context.getException()));
            return;
        }

        ServiceBusException exception = (ServiceBusException) context.getException();
        ServiceBusFailureReason reason = exception.getReason();

        if (reason == ServiceBusFailureReason.MESSAGING_ENTITY_DISABLED
            || reason == ServiceBusFailureReason.MESSAGING_ENTITY_NOT_FOUND
            || reason == ServiceBusFailureReason.UNAUTHORIZED) {
            errors.add(String.format("An unrecoverable error occurred. Stopping processing with reason %s: %s%n",
                reason, exception.getMessage()));
            countdownLatch.countDown();
        } else if (reason == ServiceBusFailureReason.MESSAGE_LOCK_LOST) {
            errors.add(String.format("Message lock lost for message: %s%n", context.getException()));
        } else if (reason == ServiceBusFailureReason.SERVICE_BUSY) {
            try {
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException e) {
                throw new RuntimeException("Unable to sleep for period of time", e);
            }
        } else {
            errors.add(String.format("Error source %s, reason %s, message: %s%n", context.getErrorSource(),
                reason, context.getException()));
        }
    }

    public List<String> getErrors() {
        return errors;
    }

    public void reset() {
        errors.clear();
    }
}
