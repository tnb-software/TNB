package software.tnb.openai.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenAIAccount implements Account, WithId {

    private OpenAIEndpoint chat;
    private OpenAIEndpoint embedding;

    @Override
    public String credentialsId() {
        return "tnb-openai";
    }

    public OpenAIEndpoint chat() {
        return chat;
    }

    public void setChat(OpenAIEndpoint chat) {
        this.chat = chat;
    }

    public OpenAIEndpoint embedding() {
        return embedding;
    }

    public void setEmbedding(OpenAIEndpoint embedding) {
        this.embedding = embedding;
    }

    public record OpenAIEndpoint(
        @JsonProperty("base-url") String baseUrl,
        String model,
        @JsonProperty("api-key") String apiKey
    ) {
    }
}
