package org.jboss.fuse.tnb.slack.service;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.slack.account.SlackAccount;
import org.jboss.fuse.tnb.slack.validation.SlackValidation;

import com.google.auto.service.AutoService;

@AutoService(Slack.class)
public class Slack implements Service {
    private SlackAccount account;
    private com.slack.api.Slack client;
    private SlackValidation validation;

    public SlackAccount account() {
        if (account == null) {
            account = Accounts.get(SlackAccount.class);
        }
        return account;
    }

    public com.slack.api.Slack client() {
        if (client == null) {
            client = com.slack.api.Slack.getInstance();
        }
        return client;
    }

    public SlackValidation validation() {
        if (validation == null) {
            validation = new SlackValidation(client(), account());
        }
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        client.close();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
    }
}
