package software.tnb.slack.validation;

import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;

/**
 * Class to hold the Slack message request configuration parameters
 */
public class MessageRequestConfig {

    private String channelName;

    private ConversationsHistoryRequest.ConversationsHistoryRequestBuilder conversationsHistoryRequestBuilder;

    public MessageRequestConfig() {
        conversationsHistoryRequestBuilder = ConversationsHistoryRequest.builder();
    }

    public MessageRequestConfig setOldest(String oldest) {
        conversationsHistoryRequestBuilder.oldest(oldest);
        return this;
    }

    public MessageRequestConfig setLatest(String latest) {
        conversationsHistoryRequestBuilder.latest(latest);
        return this;
    }

    public MessageRequestConfig setLimit(Integer limit) {
        conversationsHistoryRequestBuilder.limit(limit);
        return this;
    }

    public MessageRequestConfig setInclusive(boolean inclusive) {
        conversationsHistoryRequestBuilder.inclusive(inclusive);
        return this;
    }

    public String getChannelName() {
        return channelName;
    }

    public MessageRequestConfig setChannelName(String channelName) {
        this.channelName = channelName;
        return this;
    }

    public MessageRequestConfig setChannel(String channelId) {
        conversationsHistoryRequestBuilder.channel(channelId);
        return this;
    }

    public ConversationsHistoryRequest.ConversationsHistoryRequestBuilder getBuilder() {
        return conversationsHistoryRequestBuilder;
    }
}
