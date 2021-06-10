package org.jboss.fuse.tnb.sqs.validation;

import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.sqs.account.SQSAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

public class SQSValidation {
    private static final Logger LOG = LoggerFactory.getLogger(SQSValidation.class);

    private final SqsClient client;
    private final SQSAccount account;

    public SQSValidation(SqsClient client, SQSAccount account) {
        this.client = client;
        this.account = account;
    }

    public void createQueue(String name) {
        LOG.debug("Creating SQS queue {}", name);
        final CreateQueueResponse queue = client.createQueue(b -> b.queueName(name));
        WaitUtils.waitFor(() -> queueExists(queue.queueUrl()), "Waiting until the queue " + name + " is created");
    }

    /**
     * Add this access policy to allow AWS users to use SQS queue as SNS topic subscriber.
     *
     * @param queue
     * @see <a href="https://camel.apache.org/components/3.10.x/aws2-sns-component.html#_sns_fifo">Camel SNS documentation</a>
     */
    public void setPermissiveAccessPolicy(String queue) {
        Map<QueueAttributeName, String> attributes = new HashMap<>();
        attributes.put(QueueAttributeName.POLICY,
            "{\n"
                + "\"Version\": \"2008-10-17\",\n"
                + "\"Id\": \"__default_policy_ID\",\n"
                + "\"Statement\": [\n"
                + "  {\n"
                + "    \"Sid\": \"__owner_statement\",\n"
                + "    \"Effect\": \"Allow\",\n"
                + "    \"Principal\": {\n"
                + "      \"AWS\": \"*\"\n"
                + "    },\n"
                + "    \"Action\": \"SQS:*\",\n"
                + "    \"Resource\": \"" + account.queueArnPrefix() + queue + "\"\n"
                + "  }\n"
                + "]\n"
                + "}");
        client.setQueueAttributes(builder -> builder.queueUrl(account.queueUrlPrefix() + queue).attributes(attributes
        ));
    }

    public void deleteQueue(String name) {
        LOG.debug("Deleting SQS queue {}", name);
        client.deleteQueue(b -> b.queueUrl(account.queueUrlPrefix() + name));
    }

    public void sendMessage(String queue, String message) {
        client.sendMessage(b -> b.queueUrl(account.queueUrlPrefix() + queue).messageBody(message));
    }

    public List<Message> getMessages(String queue, int count) {
        return WaitUtils.withTimeout(() -> {
            List<Message> messages = new ArrayList<>();
            while (messages.size() != count) {
                messages.addAll(client.receiveMessage(b -> b.queueUrl(account.queueUrlPrefix() + queue).maxNumberOfMessages(1)).messages());
            }
            return messages;
        });
    }

    public boolean queueExists(String queueUrl) {
        return client.listQueues().queueUrls().stream().anyMatch(queueUrl::equals);
    }
}
