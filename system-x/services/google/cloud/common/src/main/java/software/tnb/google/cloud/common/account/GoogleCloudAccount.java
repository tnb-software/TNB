package software.tnb.google.cloud.common.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

import org.json.JSONObject;

import java.util.Base64;

public class GoogleCloudAccount implements Account, WithId {

    // this needs to be in base64 in the credentials file
    private String service_account_key;

    @Override
    public String credentialsId() {
        return "google-cloud";
    }

    private String fromJson(String key) {
        try {
            return new JSONObject(new String(Base64.getDecoder().decode(service_account_key))).get(key).toString();
        } catch (Exception e) {
            throw new RuntimeException("Unable to decode base64 service account json", e);
        }
    }

    public String projectId() {
        return fromJson("project_id");
    }

    public String privateKey() {
        return fromJson("private_key");
    }

    public String clientEmail() {
        return fromJson("client_email");
    }

    public String clientId() {
        return fromJson("client_id");
    }

    public String serviceAccountKey() {
        return service_account_key;
    }

    public void setService_account_key(String service_account_key) {
        this.service_account_key = service_account_key;
    }
}
