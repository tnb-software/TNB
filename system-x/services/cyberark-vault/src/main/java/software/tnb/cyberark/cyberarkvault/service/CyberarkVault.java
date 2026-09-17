package software.tnb.cyberark.cyberarkvault.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.cyberark.cyberarkvault.account.CyberarkVaultAccount;
import software.tnb.cyberark.cyberarkvault.validation.CyberarkVaultValidation;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class CyberarkVault extends Service<CyberarkVaultAccount, HTTPUtils, CyberarkVaultValidation> implements WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(CyberarkVault.class);

    public abstract String url();

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/conjur:1.27.0";
    }

    @Override
    protected HTTPUtils client() {
        if (client == null) {
            client = HTTPUtils.getInstance(HTTPUtils.trustAllSslClient());
        }
        return client;
    }

    @Override
    public CyberarkVaultValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new CyberarkVault validation");
            validation = new CyberarkVaultValidation(client(), url(), account());
        }
        return validation;
    }

    protected Map<String, String> containerEnvironment(String databaseUrl, int port) {
        return Map.of(
            "DATABASE_URL", databaseUrl,
            "CONJUR_DATA_KEY", account().encryptionKey(),
            "PORT", String.valueOf(port)
        );
    }

    protected String parseApiKey(String conjurctlOutput) {
        return StringUtils.substringAfter(conjurctlOutput, "API key for " + account().userName() + ": ").trim();
    }
}
