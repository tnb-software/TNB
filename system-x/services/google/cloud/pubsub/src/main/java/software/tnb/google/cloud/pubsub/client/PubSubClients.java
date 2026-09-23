package software.tnb.google.cloud.pubsub.client;

import software.tnb.google.cloud.common.account.GoogleCloudAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;

import java.io.IOException;

public class PubSubClients {
    private static final Logger LOG = LoggerFactory.getLogger(PubSubClients.class);

    private final GoogleCloudAccount account;
    private final TopicAdminClient topicAdminClient;
    private final SubscriptionAdminClient subscriptionAdminClient;

    public PubSubClients(GoogleCloudAccount account) {
        this.account = account;
        topicAdminClient = newTopicAdminClient();
        subscriptionAdminClient = newSubscriptionAdminClient();
    }

    public TopicAdminClient topicAdminClient() {
        return topicAdminClient;
    }

    public SubscriptionAdminClient subscriptionAdminClient() {
        return subscriptionAdminClient;
    }

    private TopicAdminClient newTopicAdminClient() {
        LOG.debug("Creating new pub/sub topic admin client");

        try {
            TopicAdminSettings topicAdminSettings =
                TopicAdminSettings.newBuilder().setCredentialsProvider(credentialsProvider()).build();
            return TopicAdminClient.create(topicAdminSettings);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create topic admin client: ", e);
        }
    }

    private SubscriptionAdminClient newSubscriptionAdminClient() {
        LOG.debug("Creating new pub/sub subscription admin client");

        try {
            SubscriptionAdminSettings subscriptionAdminSettings =
                SubscriptionAdminSettings.newBuilder().setCredentialsProvider(credentialsProvider()).build();
            return SubscriptionAdminClient.create(subscriptionAdminSettings);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create subscription admin client: ", e);
        }
    }

    public CredentialsProvider credentialsProvider() {
        try {
            return FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(account.serviceAccountKeyStream()));
        } catch (IOException e) {
            throw new RuntimeException("Unable to extract service account key: ", e);
        }
    }

    public void close() {
        if (topicAdminClient != null) {
            topicAdminClient.close();
        }
        if (subscriptionAdminClient != null) {
            subscriptionAdminClient.close();
        }
    }
}
