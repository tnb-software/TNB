package org.jboss.fuse.tnb.slack.validation;

import static org.junit.jupiter.api.Assertions.fail;

import org.jboss.fuse.tnb.slack.account.SlackAccount;

import com.slack.api.Slack;
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
    private final Slack client;
    private final SlackAccount account;

    public SlackValidation(Slack client, SlackAccount account) {
        this.client = client;
        this.account = account;
    }

    public void sendMessage(String text) {
        try {
            ConversationsListResponse conversationsList =
                client.methods().conversationsList(ConversationsListRequest.builder().token(account.token()).build());
            Conversation chann = conversationsList.getChannels().stream().filter(c -> c.getName().contains("tests")).findFirst().get();
            final ChatPostMessageResponse chatPostMessageResponse = client.methods().chatPostMessage(ChatPostMessageRequest.builder()
                .token(account.token())
                .channel(chann.getId())
                .text(text)
                .build());
//            LOG.debug("Send message response: " + chatPostMessageResponse);
        } catch (Exception e) {
            fail("Unable to send message to slack: ", e);
        }
    }

    public List<String> getMessages() {
        try {
            ConversationsListResponse conversationsList =
                client.methods().conversationsList(ConversationsListRequest.builder().token(account.token()).build());
            Conversation chann = conversationsList.getChannels().stream()
                .filter(c -> c.getName().equals("tests")).findFirst().get();
            return client.methods().conversationsHistory(ConversationsHistoryRequest.builder()
                .token(account.token())
                .channel(chann.getId())
                .build())
                .getMessages().stream().map(Message::getText).collect(Collectors.toList());
        } catch (Exception e) {
            fail("Unable to get slack message history: ", e);
        }
        return null;
    }
}
