package software.tnb.telegram.validation;

import software.tnb.telegram.service.Telegram;
import software.tnb.telegram.validation.model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class TelegramValidation {
    private final Telegram client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger LOG = LoggerFactory.getLogger(TelegramValidation.class);

    public TelegramValidation(Telegram client) {
        this.client = client;
    }

    public void sendMessage(String text) {
        LOG.debug("Sending message {} from telegram-client", text);
        try {
            client.execInContainer("python3", "/app/send_message.py", text);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }

    public List<Message> getLastNMessages(int n) {
        LOG.debug("Get last " + n + " messages: ");
        try {
            String response = client.execInContainer("python3", "/app/get_messages.py", n + "");
            LOG.debug("Received messages: {}", response);
            return objectMapper.readValue(response, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to get messages", e);
        }
    }
}

