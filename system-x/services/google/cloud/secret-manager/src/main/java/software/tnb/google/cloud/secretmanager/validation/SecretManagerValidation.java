package software.tnb.google.cloud.secretmanager.validation;

import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.ProjectName;
import com.google.cloud.secretmanager.v1.Replication;
import com.google.cloud.secretmanager.v1.Secret;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretName;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.cloud.secretmanager.v1.SecretVersion;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

public class SecretManagerValidation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(SecretManagerValidation.class);
    private final SecretManagerServiceClient client;
    private final String projectId;

    public SecretManagerValidation(SecretManagerServiceClient client, String projectId) {
        this.client = client;
        this.projectId = projectId;
    }

    public void createSecret(String secretId) {
        Secret secret = Secret.newBuilder()
                .setReplication(
                    Replication.newBuilder().setAutomatic(
                        Replication.Automatic.newBuilder().build())
                    .build())
                .build();
        ProjectName projectName = ProjectName.of(projectId);
        client.createSecret(projectName, secretId, secret);
        LOG.debug("Created secret {}", secretId);
    }

    public void deleteSecret(String secretId) {
        SecretName secretName = SecretName.of(projectId, secretId);
        client.deleteSecret(secretName);
        LOG.debug("Deleted secret {}", secretId);
    }

    public void addSecretVersion(String secretId, String secretData) {
        byte[] data = secretData.getBytes();
        SecretName secretName = SecretName.of(projectId, secretId);
        Checksum checksum = new CRC32C();
        checksum.update(data, 0, data.length);

        SecretPayload payload =
            SecretPayload.newBuilder()
                .setData(ByteString.copyFrom(data))
                .setDataCrc32C(checksum.getValue())
                .build();

        SecretVersion version = client.addSecretVersion(secretName, payload);
        LOG.debug("Added secret version {}", version.getName());
    }

    public String getSecretVersion(String secretId, String versionId) {
        SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, versionId);
        AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);
        byte[] data = response.getPayload().getData().toByteArray();
        Checksum checksum = new CRC32C();
        checksum.update(data, 0, data.length);

        if (response.getPayload().getDataCrc32C() != checksum.getValue()) {
            LOG.error("Data corruption detected.");
            throw new IllegalStateException("Data corruption, incorrect secret checksum");
        }

        String payload = response.getPayload().getData().toStringUtf8();
        LOG.debug("Accessed secret {}, version {}. Data: {}", secretId, versionId, payload);
        return payload;
    }

    public List<String> listSecrets() {
        ProjectName projectName = ProjectName.of(projectId);
        List<String> secrets = new ArrayList<>();
        SecretManagerServiceClient.ListSecretsPagedResponse pagedResponse = client.listSecrets(projectName);
        pagedResponse
            .iterateAll()
            .forEach(
                secret -> secrets.add(SecretName.parse(secret.getName()).getSecret())
            );
        return secrets;
    }
}
