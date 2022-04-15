package org.jboss.fuse.tnb.slack.validation;

import static org.junit.jupiter.api.Assertions.fail;

import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.slack.account.SlackAccount;
import org.jboss.fuse.tnb.slack.validation.util.ThrowingFunction;

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
import com.slack.api.model.Conversation;
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

    public void sendMessage(String text) {
        LOG.info("Sending message {} to Slack", text);

        ConversationsListResponse conversationsList =
            invoke((c) -> c.methods().conversationsList(ConversationsListRequest.builder().token(account.token()).build()));
        Conversation chann = conversationsList.getChannels().stream().filter(c -> c.getName().contains(account.channel())).findFirst().get();
        final ChatPostMessageResponse chatPostMessageResponse = invoke((c) -> c.methods().chatPostMessage(ChatPostMessageRequest.builder()
            .token(account.token())
            .channel(chann.getId())
            .text(text)
            .build()));
        LOG.debug("Send message response: " + chatPostMessageResponse);
    }

    public List<String> getMessages() {
        LOG.debug("Getting Slack messages");
        ConversationsListResponse conversationsList = null;
        conversationsList = invoke(c -> c.methods().conversationsList(ConversationsListRequest.builder().token(account.token()).build()));
        String channelId = conversationsList.getChannels().stream().filter(c -> c.getName().equals(account.channel())).findFirst().get().getId();
        return invoke((c) -> client.methods().conversationsHistory(ConversationsHistoryRequest.builder()
            .token(account.token())
            .channel(channelId)
            .build()))
            .getMessages().stream().map(Message::getText).collect(Collectors.toList());
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
