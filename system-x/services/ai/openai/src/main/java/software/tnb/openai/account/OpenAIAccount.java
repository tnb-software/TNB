package software.tnb.openai.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class OpenAIAccount implements Account, WithId {

    private String apiUrl;
    private String apiKey;

    @Override
    public String credentialsId() {
        return "litemaas";
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
