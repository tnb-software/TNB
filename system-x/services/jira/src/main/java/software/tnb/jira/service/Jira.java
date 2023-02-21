package software.tnb.jira.service;

import software.tnb.common.account.AccountFactory;
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
public class Jira implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(Jira.class);

    private JiraAccount account;
    private ApiClient client;
    private JiraValidation validation;

    public JiraAccount account() {
        if (account == null) {
            LOG.debug("Creating new Jira account");
            account = AccountFactory.create(JiraAccount.class);
        }
        return account;
    }

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

    public JiraValidation validation() {
        return validation;
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
