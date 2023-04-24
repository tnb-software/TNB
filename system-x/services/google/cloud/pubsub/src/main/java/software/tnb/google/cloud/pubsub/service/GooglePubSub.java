package software.tnb.google.cloud.pubsub.service;

import software.tnb.common.service.Service;
import software.tnb.google.cloud.common.account.GoogleCloudAccount;
import software.tnb.google.cloud.pubsub.client.PubSubClients;
import software.tnb.google.cloud.pubsub.validation.PubSubValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(GooglePubSub.class)
public class GooglePubSub extends Service<GoogleCloudAccount, PubSubClients, PubSubValidation> {
    private static final Logger LOG = LoggerFactory.getLogger(GooglePubSub.class);

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new Google PubSub validation");
        validation = new PubSubValidation(client(), account().projectId());
    }

    @Override
    protected PubSubClients client() {
        if (client == null) {
            client = new PubSubClients(account());
        }
        return client;
    }
}
