package software.tnb.openai.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.validation.Validation;
import software.tnb.openai.account.OpenAIAccount;

import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class OpenAIValidation implements Validation {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final OpenAIAccount.OpenAIEndpoint chat;
    private final OpenAIAccount.OpenAIEndpoint embedding;

    public OpenAIValidation(OpenAIAccount.OpenAIEndpoint chat, OpenAIAccount.OpenAIEndpoint embedding) {
        this.chat = chat;
        this.embedding = embedding;
    }

    public List<String> getChatModels() {
        return getModels(chat);
    }

    public List<String> getEmbeddingModels() {
        return getModels(embedding);
    }

    @NotNull
    private List<String> getModels(OpenAIAccount.OpenAIEndpoint endpoint) {
        HTTPUtils.Response response = HTTPUtils.getInstance().get(endpoint.baseUrl() + "/models");
        try {
            JsonNode data = MAPPER.readTree(response.getBody()).get("data");
            List<String> models = new ArrayList<>();
            for (JsonNode node : data) {
                models.add(node.get("id").asText());
            }
            return models;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse models response", e);
        }
    }
}
