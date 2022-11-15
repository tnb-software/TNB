package software.tnb.telegram.validation;

import static org.junit.jupiter.api.Assertions.fail;

import software.tnb.telegram.service.Telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class TelegramValidation {
    private final Telegram client;

    private static final Logger LOG = LoggerFactory.getLogger(TelegramValidation.class);

    public TelegramValidation(Telegram client) {
        this.client = client;
    }

    public void sendMessage(String text) {
        LOG.debug("Send message " + text + " from telegram-client ");
        try {
            client.execInContainer("python3", "/app/send_message.py", text);
        } catch (Exception e) {
            fail("Failed to send message", e);
        }
    }

    public List<String> getLastNMessages(int n) {
        LOG.debug("Get last " + n + " messages: ");
        List<String> messages;
        try {
            String messagesInStr = client.execInContainer("python3", "/app/get_messages.py", n + "");
            LOG.debug(messagesInStr);
            messages = Arrays.asList(messagesInStr.split("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get messages", e);
        }
        return messages;
    }
}

