package org.jboss.fuse.tnb.slack.account;

import org.jboss.fuse.tnb.common.account.Account;
import org.jboss.fuse.tnb.common.account.WithId;

public class SlackAccount implements Account, WithId {
    private String webhook_url;
    private String token;
    private String channel;

    @Override
    public String credentialsId() {
        return "slack-tnb";
    }

    public String webhookUrl() {
        return webhook_url;
    }

    public void setWebhook_url(String webhook_url) {
        this.webhook_url = webhook_url;
    }

    public String token() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String channel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
