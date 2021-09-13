package org.jboss.fuse.tnb.telegram.validation;

import static org.junit.jupiter.api.Assertions.fail;

import org.jboss.fuse.tnb.telegram.resource.TelegramContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class TelegramValidation {
    private final TelegramContainer container;

    private static final Logger LOG = LoggerFactory.getLogger(TelegramValidation.class);

    public TelegramValidation(TelegramContainer container) {
        this.container = container;
    }

    public void sendMessage(String text) {
        LOG.debug("Send message " + text + " from telegram-client ");
        try {
            container.execInContainer("python3", "/app/send_message.py", text);
        } catch (Exception e) {
            fail("Failed to send message", e);
        }
    }

    public List<String> getLastNMessages(int n) {
        LOG.debug("Get last " + n + " messages: ");
        List<String> messages;
        try {
            String messagesInStr = container.execInContainer("python3", "/app/get_messages.py", n + "").getStdout();
            LOG.debug(messagesInStr);
            messages = Arrays.asList(messagesInStr.split("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get messages", e);
        }
        return messages;
    }
}

