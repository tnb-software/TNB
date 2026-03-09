package software.tnb.ollama.validation;

import software.tnb.common.validation.Validation;

import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OllamaValidation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(OllamaValidation.class);
    private static final Gson GSON = new Gson();

    private final CloseableHttpClient httpClient;
    private final String baseUrl;

    public OllamaValidation(CloseableHttpClient httpClient, String baseUrl) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
    }

    public boolean isHealthy() {
        HttpGet request = new HttpGet(baseUrl + "/");
        try (ClassicHttpResponse response = httpClient.execute(request, r -> r)) {
            int statusCode = response.getCode();
            LOG.debug("Health check returned status code: {}", statusCode);
            return statusCode == HttpStatus.SC_OK;
        } catch (Exception e) {
            LOG.error("Health check failed", e);
            return false;
        }
    }

    public List<String> listModels() {
        HttpGet request = new HttpGet(baseUrl + "/api/tags");
        try {
            String body = httpClient.execute(request, response -> EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
            JsonObject json = GSON.fromJson(body, JsonObject.class);
            JsonArray models = json.getAsJsonArray("models");
            List<String> names = new ArrayList<>();
            if (models != null) {
                models.forEach(m -> names.add(m.getAsJsonObject().get("name").getAsString()));
            }
            return names;
        } catch (Exception e) {
            throw new RuntimeException("Failed to list models", e);
        }
    }

    public void pullModel(String model) {
        LOG.info("Pulling model '{}' - this may take a while", model);
        HttpPost request = new HttpPost(baseUrl + "/api/pull");
        JsonObject payload = new JsonObject();
        payload.addProperty("name", model);
        payload.addProperty("stream", false);
        request.setEntity(new StringEntity(GSON.toJson(payload), StandardCharsets.UTF_8));
        request.setHeader("Content-Type", "application/json");
        try {
            String body = httpClient.execute(request, response -> EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
            JsonObject json = GSON.fromJson(body, JsonObject.class);
            String status = json.has("status") ? json.get("status").getAsString() : "";
            LOG.info("Pull model '{}' completed with status: {}", model, status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to pull model: " + model, e);
        }
    }

    public String generate(String model, String prompt) {
        HttpPost request = new HttpPost(baseUrl + "/api/generate");
        JsonObject payload = new JsonObject();
        payload.addProperty("model", model);
        payload.addProperty("prompt", prompt);
        payload.addProperty("stream", false);
        request.setEntity(new StringEntity(GSON.toJson(payload), StandardCharsets.UTF_8));
        request.setHeader("Content-Type", "application/json");
        try {
            String body = httpClient.execute(request, response -> EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
            JsonObject json = GSON.fromJson(body, JsonObject.class);
            return json.has("response") ? json.get("response").getAsString() : body;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate text", e);
        }
    }

    public String chat(String model, List<Map<String, String>> messages) {
        HttpPost request = new HttpPost(baseUrl + "/api/chat");
        JsonObject payload = new JsonObject();
        payload.addProperty("model", model);
        payload.addProperty("stream", false);
        payload.add("messages", GSON.toJsonTree(messages));
        request.setEntity(new StringEntity(GSON.toJson(payload), StandardCharsets.UTF_8));
        request.setHeader("Content-Type", "application/json");
        try {
            String body = httpClient.execute(request, response -> EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
            JsonObject json = GSON.fromJson(body, JsonObject.class);
            if (json.has("message")) {
                return json.getAsJsonObject("message").get("content").getAsString();
            }
            return body;
        } catch (Exception e) {
            throw new RuntimeException("Failed to chat", e);
        }
    }

    public void deleteModel(String model) {
        HttpDelete request = new HttpDelete(baseUrl + "/api/delete");
        JsonObject payload = new JsonObject();
        payload.addProperty("name", model);
        request.setEntity(new StringEntity(GSON.toJson(payload), StandardCharsets.UTF_8));
        request.setHeader("Content-Type", "application/json");
        try {
            int statusCode = httpClient.execute(request, response -> response.getCode());
            LOG.info("Delete model '{}' returned status: {}", model, statusCode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete model: " + model, e);
        }
    }

    public String showModel(String model) {
        HttpPost request = new HttpPost(baseUrl + "/api/show");
        JsonObject payload = new JsonObject();
        payload.addProperty("name", model);
        request.setEntity(new StringEntity(GSON.toJson(payload), StandardCharsets.UTF_8));
        request.setHeader("Content-Type", "application/json");
        try {
            return httpClient.execute(request, response -> EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to show model: " + model, e);
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
