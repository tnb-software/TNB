package software.tnb.telegram.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.validation.Validation;
import software.tnb.telegram.validation.model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TelegramValidation implements Validation {
    private final String httpEndpoint;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HTTPUtils httpClient;

    private static final Logger LOG = LoggerFactory.getLogger(TelegramValidation.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public TelegramValidation(String httpEndpoint) {
        this.httpEndpoint = httpEndpoint;
        HTTPUtils.OkHttpClientBuilder okHttpClientBuilder = new HTTPUtils.OkHttpClientBuilder();
        okHttpClientBuilder.trustAllSslClient();
        this.httpClient = HTTPUtils.getInstance(okHttpClientBuilder.build());
    }

    public String sendMessage(String text) {
        LOG.debug("Sending message {} from telegram-client", text);
        try {
            String endpoint = httpEndpoint + "/messages";
            String requestJson = objectMapper.writeValueAsString(
                objectMapper.createObjectNode().put("text", text)
            );

            HTTPUtils.Response response = httpClient.post(endpoint, RequestBody.create(requestJson, JSON));

            if (!response.isSuccessful()) {
                throw new RuntimeException("HTTP request failed with status: " + response.getResponseCode()
                    + ", body: " + response.getBody());
            }

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }

    public List<Message> getLastNMessages(int n) {
        LOG.debug("Get last " + n + " messages");
        try {
            String endpoint = httpEndpoint + "/messages?limit=" + n;

            HTTPUtils.Response response = httpClient.get(endpoint);

            if (!response.isSuccessful()) {
                throw new RuntimeException("HTTP request failed with status: " + response.getResponseCode()
                    + ", body: " + response.getBody());
            }

            LOG.debug("Received messages: {}", response.getBody());
            return objectMapper.readValue(response.getBody(), new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to get messages", e);
        }
    }
}

