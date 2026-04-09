package software.tnb.ssh.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.ConfigurableService;
import software.tnb.ssh.account.SSHAccount;
import software.tnb.ssh.service.configuration.SSHConfiguration;
import software.tnb.ssh.validation.SSHValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.security.Security;
import java.util.Map;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.PKCS8KeyFile;
import net.schmizz.sshj.userauth.method.AuthPublickey;

public abstract class SSHServer extends ConfigurableService<SSHAccount, SSHClient, SSHValidation, SSHConfiguration>
    implements WithDockerImage {

    private static final Logger LOG = LoggerFactory.getLogger(SSHServer.class);

    public static final int SSHD_LISTENING_PORT = 22;

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/alpine-openssh:latest";
    }

    protected abstract String clientHostname();

    protected abstract int clientPort();

    public void openResources() {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            SSHClient ssh = new SSHClient();
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.connect(clientHostname(), clientPort());

            Path privateKeyPath = getConfiguration().privateKeyPath();
            if (privateKeyPath != null) {
                // Use key-based authentication
                LOG.debug("Connecting with SSH key authentication");
                PKCS8KeyFile keyFile = new PKCS8KeyFile();
                keyFile.init(privateKeyPath.toFile());
                ssh.auth(account().username(), new AuthPublickey(keyFile));
            } else {
                // Use password authentication
                LOG.debug("Connecting with password authentication");
                ssh.authPassword(account().username(), account().password());
            }

            client = ssh;
        } catch (Exception e) {
            throw new RuntimeException("Unable to create SSH client", e);
        }
    }

    public void closeResources() {
        if (validation != null) {
            validation = null;
        }
        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
            } catch (Exception e) {
                LOG.warn("Unable to disconnect SSH client", e);
            }
        }
    }

    public SSHValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new SSH validation");
            validation = new SSHValidation(client);
        }
        return validation;
    }

    public abstract String host();

    public abstract int port();

    public Map<String, String> containerEnvironment() {
        return Map.of("SSH_PASSWORD", account().password());
    }

    @Override
    protected void defaultConfiguration() {
    }
}
