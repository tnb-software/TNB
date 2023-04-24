package software.tnb.jira.service;

import software.tnb.common.service.Service;
import software.tnb.jira.account.JiraAccount;
import software.tnb.jira.validation.JiraValidation;
import software.tnb.jira.validation.generated.ApiClient;
import software.tnb.jira.validation.generated.Configuration;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(Jira.class)
public class Jira extends Service<JiraAccount, ApiClient, JiraValidation> {
    private static final Logger LOG = LoggerFactory.getLogger(Jira.class);

    @Override
    protected ApiClient client() {
        if (client == null) {
            LOG.debug("Creating new JiraRest client");

            client = Configuration.getDefaultApiClient();
            client.setBasePath(account().getJiraUrl());
            client.setUsername(account().getUsername());
            client.setPassword(account().getPassword());
        }
        return client;
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        LOG.debug("Creating new Jira validation");
        validation = new JiraValidation(client());
    }
}
