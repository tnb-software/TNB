package software.tnb.hashicorp.vault.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.hashicorp.vault.service.HashicorpVault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(HashicorpVault.class)
public class LocalHashicorpVault extends HashicorpVault implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalHashicorpVault.class);
    private HashicorpVaultContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting Hashicorp Vault");
        container = new HashicorpVaultContainer(image(), Map.of("VAULT_DEV_ROOT_TOKEN_ID", account().token()), PORT);
        container.start();
        LOG.info("Hashicorp Vault container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Hashicorp Vault container");
            container.stop();
        }
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(PORT);
    }

    @Override
    public String scheme() {
        return "http";
    }
}
