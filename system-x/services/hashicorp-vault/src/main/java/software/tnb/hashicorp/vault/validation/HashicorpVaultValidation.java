package software.tnb.hashicorp.vault.validation;

import software.tnb.common.validation.Validation;

import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.Map;

public class HashicorpVaultValidation implements Validation {
    private VaultTemplate client;

    public HashicorpVaultValidation(VaultTemplate client) {
        this.client = client;
    }

    public void createSecret(String path, Map<String, Object> entry) {

        VaultKeyValueOperations vaultKeyValueOperations = client.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.versioned());
        vaultKeyValueOperations.put(path, entry);
    }

    public Map<String, Object> readSecret(String path) {
        VaultKeyValueOperations vaultKeyValueOperations = client.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.versioned());
        VaultResponse vaultResponse = vaultKeyValueOperations.get(path);

        return vaultResponse.getData();
    }

    public void updateSecret(String path, Map<String, Object> entry) {
        VaultKeyValueOperations vaultKeyValueOperations = client.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.versioned());
        vaultKeyValueOperations.patch(path, entry);
    }
}
