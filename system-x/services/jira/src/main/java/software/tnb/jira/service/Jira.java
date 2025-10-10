package software.tnb.jira.service;

import software.tnb.common.service.Service;
import software.tnb.jira.account.JiraAccount;
import software.tnb.jira.validation.JiraValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.google.auto.service.AutoService;

import java.net.URI;
import java.net.URISyntaxException;

@AutoService(Jira.class)
public class Jira extends Service<JiraAccount, JiraRestClient, JiraValidation> {
    private static final Logger LOG = LoggerFactory.getLogger(Jira.class);

    @Override
    protected JiraRestClient client() {
        if (client == null) {
            LOG.debug("Creating new Jira client");
            try {
                client = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(new URI(account().getJiraUrl()),
                    account().getUsername(), account().getPassword());
            } catch (URISyntaxException e) {
                throw new RuntimeException("Unable to create jira client", e);
            }

        }
        return client;
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        client.close();
        client = null;
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        LOG.debug("Creating new Jira validation");
        validation = new JiraValidation(client());
    }
}
