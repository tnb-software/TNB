package software.tnb.hashicorp.vault.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.hashicorp.vault.service.HashicorpVault;

import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(HashicorpVault.class)
public class LocalHashicorpVault extends HashicorpVault implements ContainerDeployable<HashicorpVaultContainer> {
    private final HashicorpVaultContainer container =
        new HashicorpVaultContainer(image(), Map.of("VAULT_DEV_ROOT_TOKEN_ID", account().token()), PORT);

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

    @Override
    public HashicorpVaultContainer container() {
        return container;
    }
}
