package software.tnb.google.cloud.common.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class GoogleCloudAccount implements Account, WithId {
    // plain json string
    private String serviceAccountKey;
    // same as ^, in case the key is loaded from the env variable, the json is interpreted as a map, so it needs to be loaded into this variable
    private Map<String, String> serviceAccountKeyMap;

    @Override
    public String credentialsId() {
        return "google_cloud";
    }

    private String getKey() {
        if (serviceAccountKey == null) {
            serviceAccountKey = toJson(serviceAccountKeyMap);
        }
        return serviceAccountKey;
    }

    private String fromJson(String key) {
        return new JSONObject(getKey()).get(key).toString();
    }

    private String toJson(Map<String, String> key) {
        try {
            return new ObjectMapper().writeValueAsString(key);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to convert map to json: ", e);
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

    /**
     * Get Google cloud service account as an InputStream
     *
     * @return InputStream representing the service account
     */
    public InputStream serviceAccountKeyStream() {
        return new ByteArrayInputStream(getKey().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Get Google cloud service account
     *
     * @return Base64 encoded service account JSON
     */
    public String serviceAccountKey() {
        return new String(Base64.getEncoder().encode(getKey().getBytes(StandardCharsets.UTF_8)));
    }

    public void setServiceAccountKey(String serviceAccountKey) {
        this.serviceAccountKey = serviceAccountKey;
    }

    public void setServiceAccountKeyMap(Map<String, String> serviceAccountKeyMap) {
        this.serviceAccountKeyMap = serviceAccountKeyMap;
    }
}
