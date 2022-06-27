package software.tnb.slack.validation;

import static org.junit.jupiter.api.Assertions.fail;

import software.tnb.common.utils.WaitUtils;
import software.tnb.slack.account.SlackAccount;
import software.tnb.slack.validation.util.ThrowingFunction;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.SlackApiTextResponse;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.request.conversations.ConversationsListRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsListResponse;
import com.slack.api.model.ConversationType;
import com.slack.api.model.Message;

import java.util.List;
import java.util.stream.Collectors;

public class SlackValidation {
    private static final Logger LOG = LoggerFactory.getLogger(SlackValidation.class);

    private final Slack client;
    private final SlackAccount account;

    public SlackValidation(Slack client, SlackAccount account) {
        this.client = client;
        this.account = account;
    }

    public void sendMessageToChannelId(String text, String conversationId) {
        LOG.debug("Sending message {} to Slack conversation with id {}", text, conversationId);
        final ChatPostMessageResponse chatPostMessageResponse = invoke((c) -> c.methods().chatPostMessage(ChatPostMessageRequest.builder()
            .token(account.token())
            .channel(conversationId)
            .text(text)
            .build()));
        if (!chatPostMessageResponse.isOk()) {
            LOG.debug("Send message response: " + chatPostMessageResponse);
        }
    }

    public void sendMessageToChannelName(String text, String channelName) {
        String channelId = getChannelId(channelName);
        sendMessageToChannelId(text, channelId);
    }

    public List<String> getMessages(MessageRequestConfig messageRequestConfig) {
        return invoke((c) -> client.methods().conversationsHistory(buildConversationRequest(messageRequestConfig)))
            .getMessages().stream().map(Message::getText).collect(Collectors.toList());
    }

    public List<String> getMessagesFromChannelId(String conversationId) {
        return invoke((c) -> client.methods().conversationsHistory(ConversationsHistoryRequest.builder()
            .token(account.token())
            .channel(conversationId)
            .build()))
            .getMessages().stream().map(Message::getText).collect(Collectors.toList());
    }

    public List<String> getMessagesFromChannelName(String channelName) {
        ConversationsListResponse conversationsList;
        conversationsList = invoke(c -> c.methods().conversationsList(ConversationsListRequest.builder()
            .token(account.token())
            .types(List.of(ConversationType.IM,
                ConversationType.MPIM,
                ConversationType.PRIVATE_CHANNEL,
                ConversationType.PUBLIC_CHANNEL))
            .build()));
        String channelId = conversationsList.getChannels().stream().filter(c -> c.getName().equals(channelName)).findFirst().get().getId();
        return getMessagesFromChannelId(channelId);
    }

    private String getChannelId(String channelName) {
        ConversationsListResponse conversationsList =
            invoke((c) -> c.methods().conversationsList(ConversationsListRequest.builder().token(account.token()).build()));
        return conversationsList.getChannels().stream().filter(c -> c.getName().contains(channelName)).findFirst().get().getId();
    }

    private ConversationsHistoryRequest buildConversationRequest(MessageRequestConfig config) {
        ConversationsHistoryRequest.ConversationsHistoryRequestBuilder builder = config.getBuilder();
        if (config.getChannelName() != null) {
            builder.channel(getChannelId(config.getChannelName()));
        }
        return builder
            .token(account.token())
            .build();
    }

    @NotNull
    private <T extends SlackApiTextResponse> T invoke(ThrowingFunction<Slack, T> consumer) {
        try {
            return consumer.apply(client);
        } catch (SlackApiException e) {
            if (e.getResponse().code() == 429) {
                String retryAfter = e.getResponse().header("Retry-After");
                if (retryAfter != null) {
                    int wait = Integer.parseInt(retryAfter);
                    LOG.warn("Slack returned HTTP 429 API Limited, will sleep for {} seconds", wait);
                    WaitUtils.sleep((wait + 1) * 1000L);
                    return invoke(consumer);
                } else {
                    fail("Got HTTP 429 API Limited, but no Retry-After header was set");
                }
            } else {
                fail("Exception from Slack API", e);
            }
        } catch (Exception e) {
            fail("Unable to invoke Slack API", e);
        }

        return null;
    }
}
