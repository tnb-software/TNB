package org.jboss.fuse.tnb.jira.service;

import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.jira.account.JiraAccount;
import org.jboss.fuse.tnb.jira.validation.JiraValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.google.auto.service.AutoService;

import java.net.URI;

@AutoService(Jira.class)
public class Jira implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(Jira.class);

    private JiraAccount account;
    private JiraRestClient client;
    private JiraValidation validation;

    public JiraAccount account() {
        if (account == null) {
            LOG.debug("Creating new Jira account");
            account = Accounts.get(JiraAccount.class);
        }
        return account;
    }

    protected JiraRestClient client() {
        if (client == null) {
            LOG.debug("Creating new JiraRest client");

            client = new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create(account().getJiraUrl()), account().getUsername(), account().getPassword());
        }
        return client;
    }

    public JiraValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new Jira validation");
            validation = new JiraValidation(client(), account());
        }
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {

    }
}
