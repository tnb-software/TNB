package software.tnb.google.cloud.pubsub.validation;

import software.tnb.common.validation.Validation;
import software.tnb.google.cloud.pubsub.client.PubSubClients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.DeadLetterPolicy;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.TopicName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PubSubValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(PubSubValidation.class);

    private final PubSubClients clients;
    private final String projectId;

    public PubSubValidation(PubSubClients clients, String projectId) {
        this.clients = clients;
        this.projectId = projectId;
    }

    public void createTopic(String name) {
        clients.topicAdminClient().createTopic(TopicName.of(projectId, name));
        LOG.info("Created google pubsub topic: {}", name);
    }

    public void deleteTopic(String name) {
        clients.topicAdminClient().deleteTopic(TopicName.of(projectId, name));
        LOG.info("Deleted google pubsub topic: {}", name);
    }

    public void createSubscription(String name, String topicName) {
        clients.subscriptionAdminClient().createSubscription(
            SubscriptionName.of(projectId, name),
            TopicName.of(projectId, topicName),
            PushConfig.getDefaultInstance(),
            10
        );
        LOG.info("Created google pubsub subscription: {}", name);
    }

    /*
        DLQ requires the automatically created service account named "service-<accountid>@gcp-sa-pubsub.iam.gserviceaccount.com"
         to have at least publisher role for the pub/sub topics
     */
    public void createSubscriptionWithDLQ(String name, String topicName, String dlqTopic, int retries) {
        clients.subscriptionAdminClient().createSubscription(Subscription.newBuilder()
                .setName(SubscriptionName.of(projectId, name).toString())
                .setTopic(ProjectTopicName.of(projectId, topicName).toString())
                .setDeadLetterPolicy(DeadLetterPolicy.newBuilder()
                    .setDeadLetterTopic(ProjectTopicName.of(projectId, dlqTopic).toString())
                    .setMaxDeliveryAttempts(retries)
                    .build())
            .build());
        LOG.info("Created google pubsub subscription: {} with DLQ topic {}", name, dlqTopic);
    }

    public void deleteSubscription(String name) {
        clients.subscriptionAdminClient().deleteSubscription(SubscriptionName.of(projectId, name));
        LOG.info("Deleted google pubsub subscription: {}", name);
    }

    public void publishMessage(String topic, String message) {
        Publisher publisher = null;
        try {
            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(TopicName.of(projectId, topic)).setCredentialsProvider(clients.credentialsProvider()).build();

            ByteString data = ByteString.copyFromUtf8(message);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

            String messageId = publisher.publish(pubsubMessage).get();
            if (!messageId.isEmpty()) {
                LOG.info("Published message {} into topic {}", message, topic);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to publish message to topic", e);
        } finally {
            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
                try {
                    publisher.awaitTermination(1, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    LOG.warn("Unable to terminate publisher", e);
                }
            }
        }
    }

    /**
     * will listen on the specified subscription for specified duration of seconds
     *
     * @param seconds wait duration
     * @param subscriptionId subscription id
     * @return list of messages
     */
    public List<String> receiveMessageFor(int seconds, String subscriptionId) {
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);

        List<PubsubMessage> messageList = new ArrayList<>();

        // Instantiate an asynchronous message receiver.
        MessageReceiver receiver =
            (PubsubMessage message, AckReplyConsumer consumer) -> {
                // Handle incoming message, then ack the received message.
                messageList.add(message);
                consumer.ack();
            };

        Subscriber subscriber = null;
        try {
            subscriber = Subscriber.newBuilder(subscriptionName, receiver).setCredentialsProvider(clients.credentialsProvider()).build();
            // Start the subscriber.
            subscriber.startAsync().awaitRunning();
            LOG.info("Listening for messages on {}", subscriptionName.toString());
            subscriber.awaitTerminated(seconds, TimeUnit.SECONDS);
        } catch (TimeoutException timeoutException) {
            subscriber.stopAsync();
        }

        return messageList.stream().map(m -> m.getData().toStringUtf8()).toList();
    }
}
