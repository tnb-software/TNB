package software.tnb.openai.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.validation.Validation;
import software.tnb.openai.account.OpenAIAccount;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class OpenAIValidation implements Validation {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final OpenAIAccount account;

    public OpenAIValidation(OpenAIAccount account) {
        this.account = account;
    }

    public List<String> getModels() {
        HTTPUtils.Response response = HTTPUtils.getInstance().get(account.getApiUrl() + "/models");
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
