package software.tnb.google.cloud.common.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class GoogleCloudAccount implements Account, WithId {
    // this needs to be in base64 in the credentials file
    private String serviceAccountKey;

    @Override
    public String credentialsId() {
        return "google_cloud";
    }

    private String fromJson(String key) {
        return new JSONObject(serviceAccountKey).get(key).toString();
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

    /**
     * Get Google cloud service account as an InputStream
     * @return InputStream representing the service account
     */
    public InputStream serviceAccountKeyStream() {
        return new ByteArrayInputStream(serviceAccountKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Get Google cloud service account
     * @return Base64 encoded service account JSON
     */
    public String serviceAccountKey() {
        return new String(Base64.getEncoder().encode(serviceAccountKey.getBytes(StandardCharsets.UTF_8)));
    }

    public void setServiceAccountKey(String serviceAccountKey) {
        this.serviceAccountKey = serviceAccountKey;
    }
}
