package software.tnb.ssh.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.ssh.service.SSHServer;

import com.google.auto.service.AutoService;

import java.nio.file.Files;
import java.nio.file.Path;

@AutoService(SSHServer.class)
public class LocalSSHServer extends SSHServer implements ContainerDeployable<SSHServerContainer> {
    private SSHServerContainer container;

    @Override
    public String containerServiceVersion() {
        try {
            String v = container().execInContainer("sh", "-c", "ssh -V 2>&1 | head -1 | sed 's|.*OpenSSH_||;s|[, ].*||'")
                .getStdout().trim();
            return v.isEmpty() ? null : v;
        } catch (Exception e) {
            LOG.debug("Failed to detect OpenSSH version from container", e);
            return null;
        }
    }

    @Override
    protected String clientHostname() {
        return host();
    }

    @Override
    protected int clientPort() {
        return port();
    }

    public void openResources() {
        super.openResources();
    }

    public void closeResources() {
        super.closeResources();
    }

    @Override
    public String host() {
        return this.container.getHost();
    }

    @Override
    public int port() {
        return this.container.getMappedPort(SSHServer.SSHD_LISTENING_PORT);
    }

    @Override
    public SSHServerContainer container() {
        return container;
    }

    @Override
    public void deploy() {
        // Validate public key file exists if configured
        Path keyPath = getConfiguration().publicKeyPath();
        if (keyPath != null && !Files.exists(keyPath)) {
            throw new RuntimeException("SSH public key file not found: " + keyPath.toAbsolutePath());
        }

        container = new SSHServerContainer(image(), containerEnvironment(), keyPath);

        ContainerDeployable.super.deploy();
    }
}
