package org.jboss.fuse.tnb.slack.account;

import org.jboss.fuse.tnb.common.account.Account;
import org.jboss.fuse.tnb.common.account.WithId;

public class SlackAccount implements Account, WithId {
    private String token;
    private String webhook_url;
    private String channel;
    private String channel_id;

    private String webhook_url_private;
    private String channel_private;
    private String channel_id_private;

    private String webhook_url_im;
    private String channel_im;
    private String channel_id_im;

    private String webhook_url_mpim;
    private String channel_mpim;
    private String channel_id_mpim;

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

    public String channelId() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String webhookUrlPrivate() {
        return webhook_url_private;
    }

    public void setWebhook_url_private(String webhook_url_private) {
        this.webhook_url_private = webhook_url_private;
    }

    public String channelPrivate() {
        return channel_private;
    }

    public void setChannel_private(String channel_private) {
        this.channel_private = channel_private;
    }

    public String channelIdPrivate() {
        return channel_id_private;
    }

    public void setChannel_id_private(String channel_id_private) {
        this.channel_id_private = channel_id_private;
    }

    public String webhookUrlIm() {
        return webhook_url_im;
    }

    public void setWebhook_url_im(String webhook_url_im) {
        this.webhook_url_im = webhook_url_im;
    }

    public String channelIm() {
        return channel_im;
    }

    public void setChannel_im(String channel_im) {
        this.channel_im = channel_im;
    }

    public String channelIdIm() {
        return channel_id_im;
    }

    public void setChannel_id_im(String channel_id_im) {
        this.channel_id_im = channel_id_im;
    }

    public String webhookUrlMpim() {
        return webhook_url_mpim;
    }

    public void setWebhook_url_mpim(String webhook_url_mpim) {
        this.webhook_url_mpim = webhook_url_mpim;
    }

    public String channelMpim() {
        return channel_mpim;
    }

    public void setChannel_mpim(String channel_mpim) {
        this.channel_mpim = channel_mpim;
    }

    public String channelIdMpim() {
        return channel_id_mpim;
    }

    public void setChannel_id_mpim(String channel_id_mpim) {
        this.channel_id_mpim = channel_id_mpim;
    }
}
