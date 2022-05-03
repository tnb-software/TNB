package org.jboss.fuse.tnb.slack.account;

import org.jboss.fuse.tnb.common.account.Account;
import org.jboss.fuse.tnb.common.account.WithId;

import java.util.Map;

/**
 * Requires following slack account definition:
 *
 *   slack-tnb:
 *     credentials:
 *       token: [token]
 *       channels:
 *         [identifier]:
 *           name: [channelName] (optional, if not present, identifier is used)
 *           webhook_url: [webhook url]
 *           channel_id: [channel id]
 *         ...
 */
public class SlackAccount implements Account, WithId {
    private String token;
    private Map<String, ChannelAccount> channels;

    @Override
    public String credentialsId() {
        return "slack-tnb";
    }

    public String token() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String channelId(String channelName) {
        return getChannel(channelName).channelId();
    }

    public String webhookUrl(String channelName) {
        return getChannel(channelName).webhookUrl();
    }

    public String channel(String channelName) {
        ChannelAccount account = getChannel(channelName);
        return account.name() == null ? channelName : account.name();
    }

    public void setChannels(Map<String, ChannelAccount> channels) {
        this.channels = channels;
    }

    private ChannelAccount getChannel(String channelName) {
        ChannelAccount account = channels.get(channelName);
        if (account == null) {
            throw new IllegalArgumentException("Unknown channel " + channelName);
        }
        return account;
    }

    public static class ChannelAccount {
        private String name;
        private String webhook_url;
        private String channel_id;

        public String name() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String webhookUrl() {
            return webhook_url;
        }

        public void setWebhook_url(String webhook_url) {
            this.webhook_url = webhook_url;
        }

        public String channelId() {
            return channel_id;
        }

        public void setChannel_id(String channel_id) {
            this.channel_id = channel_id;
        }
    }

}
