package software.tnb.slack.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

import java.util.Map;

/**
 * Requires following slack account definition:
 *
 *   slack:
 *     credentials:
 *       token: [token]
 *       channels:
 *         [identifier]:
 *           name: [channelName] (optional, if not present, identifier is used)
 *           webhookUrl: [webhook url]
 *           channelId: [channel id]
 *         ...
 */
public class SlackAccount implements Account, WithId {
    private String token;
    private Map<String, ChannelAccount> channels;

    @Override
    public String credentialsId() {
        return "slack";
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
        private String webhookUrl;
        private String channelId;

        public String name() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String webhookUrl() {
            return webhookUrl;
        }

        public void setWebhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
        }

        public String channelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }
    }

}
