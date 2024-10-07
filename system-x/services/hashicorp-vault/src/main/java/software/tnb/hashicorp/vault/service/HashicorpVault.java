package software.tnb.hashicorp.vault.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.hashicorp.vault.account.HashicorpVaultAccount;
import software.tnb.hashicorp.vault.validation.HashicorpVaultValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;

public abstract class HashicorpVault extends Service<HashicorpVaultAccount, VaultTemplate, HashicorpVaultValidation> implements WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(HashicorpVault.class);

    protected static final int PORT = 8200;

    public abstract String host();

    public abstract int port();

    public abstract String scheme();

    public String defaultImage() {
        return "quay.io/rh_integration/hashicorp/vault:1.17";
    }

    @Override
    public HashicorpVaultValidation validation() {
        return new HashicorpVaultValidation(client());
    }

    @Override
    protected VaultTemplate client() {
        VaultEndpoint vaultEndpoint = new VaultEndpoint();
        vaultEndpoint.setHost(host());
        vaultEndpoint.setPort(port());
        vaultEndpoint.setScheme(scheme());

        return new VaultTemplate(
            vaultEndpoint,
            new TokenAuthentication(account().token())
        );
    }
}
