package software.tnb.mina.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.ConfigurableService;
import software.tnb.mina.account.MinaAccount;
import software.tnb.mina.service.configuration.MinaConfiguration;
import software.tnb.mina.validation.MinaValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.security.Security;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

public abstract class Mina extends ConfigurableService<MinaAccount, SSHClient, MinaValidation, MinaConfiguration>
    implements WithDockerImage {

    private static final Logger LOG = LoggerFactory.getLogger(Mina.class);

    public static final int SSHD_LISTENING_PORT = 22;

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/mina-sshd:2.14.0";
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
                LOG.debug("Connecting with SSH key authentication");
                ssh.authPublickey(account().username(), privateKeyPath.toString());
            } else {
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

    public MinaValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new Mina validation");
            validation = new MinaValidation(client);
        }
        return validation;
    }

    public abstract String host();

    public abstract int port();

    @Override
    protected void defaultConfiguration() {
    }
}
