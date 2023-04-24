package software.tnb.azure.storage.queue.validation;

import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.storage.queue.QueueServiceClient;
import com.azure.storage.queue.models.QueueMessageItem;

import java.util.ArrayList;
import java.util.List;

public class StorageQueueValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(StorageQueueValidation.class);

    private final QueueServiceClient client;

    public StorageQueueValidation(QueueServiceClient client) {
        this.client = client;
    }

    public void createQueue(String name) {
        LOG.debug("Creating queue {}", name);
        client.createQueue(name);
    }

    public void deleteQueue(String name) {
        LOG.debug("Deleting queue {}", name);
        client.deleteQueue(name);
    }

    public void sendMessage(String queue, String message) {
        LOG.debug("Sending message {} to queue {}", message, queue);
        client.getQueueClient(queue).sendMessage(message);
    }

    public QueueMessageItem getMessage(String queue) {
        LOG.debug("Getting single message from queue {}", queue);
        return client.getQueueClient(queue).receiveMessage();
    }

    public List<QueueMessageItem> getMessages(String queue, int count) {
        LOG.debug("Getting {} messages from queue {}", count, queue);
        List<QueueMessageItem> messages = new ArrayList<>();
        client.getQueueClient(queue).receiveMessages(count).forEach(messages::add);
        return messages;
    }

    public int getQueueSize(String queue) {
        return client.getQueueClient(queue).getProperties().getApproximateMessagesCount();
    }
}
