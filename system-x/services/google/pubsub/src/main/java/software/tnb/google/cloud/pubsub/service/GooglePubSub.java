package software.tnb.google.cloud.pubsub.service;

import software.tnb.common.account.Accounts;
import software.tnb.common.service.Service;
import software.tnb.google.cloud.common.account.GoogleCloudAccount;
import software.tnb.google.cloud.pubsub.validation.PubSubValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auto.service.AutoService;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@AutoService(GooglePubSub.class)
public class GooglePubSub implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(GooglePubSub.class);

    private GoogleCloudAccount account;
    private PubSubValidation validation;

    private TopicAdminClient topicAdminClient;
    private SubscriptionAdminClient subscriptionAdminClient;

    public GoogleCloudAccount account() {
        if (account == null) {
            account = Accounts.get(GoogleCloudAccount.class);
        }
        return account;
    }

    protected TopicAdminClient topicAdminClient() throws IOException {
        LOG.debug("Creating new Google cloud topic admin client");

        TopicAdminSettings topicAdminSettings =
            TopicAdminSettings.newBuilder().setCredentialsProvider(credentialsProvider()).build();
        return TopicAdminClient.create(topicAdminSettings);
    }

    protected SubscriptionAdminClient subscriptionAdminClient() throws IOException {
        LOG.debug("Creating new Google subscription admin client");

        SubscriptionAdminSettings subscriptionAdminSettings =
            SubscriptionAdminSettings.newBuilder().setCredentialsProvider(credentialsProvider()).build();
        return SubscriptionAdminClient.create(subscriptionAdminSettings);
    }

    private CredentialsProvider credentialsProvider() throws IOException {
        InputStream serviceAccountKey = new ByteArrayInputStream(Base64.getDecoder().decode(account().serviceAccountKey()));
        return FixedCredentialsProvider.create(GoogleCredentials.fromStream(serviceAccountKey));
    }

    public PubSubValidation validation() {
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        topicAdminClient.close();
        subscriptionAdminClient.close();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new Google PubSub validation");

        topicAdminClient = topicAdminClient();
        subscriptionAdminClient = subscriptionAdminClient();

        validation = new PubSubValidation(topicAdminClient, subscriptionAdminClient, credentialsProvider(), account().projectId());
    }
}
