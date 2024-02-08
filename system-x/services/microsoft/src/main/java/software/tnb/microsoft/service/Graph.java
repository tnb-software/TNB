package software.tnb.microsoft.service;

import software.tnb.common.service.Service;
import software.tnb.microsoft.account.MicrosoftAccount;
import software.tnb.microsoft.validation.GraphValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.google.auto.service.AutoService;
import com.microsoft.graph.serviceclient.GraphServiceClient;

@AutoService(Graph.class)
public class Graph extends Service<MicrosoftAccount, GraphServiceClient, GraphValidation> {
    private static final Logger LOG = LoggerFactory.getLogger(Graph.class);

    protected GraphServiceClient client() {
        final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
            .clientId(account().clientId())
            .clientSecret(account().clientSecret())
            .tenantId(account().tenantId())
            .build();

        return new GraphServiceClient(clientSecretCredential, "https://graph.microsoft.com/.default");
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new Graph validation");
        validation = new GraphValidation(client());
    }
}
