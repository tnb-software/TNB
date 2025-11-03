package software.tnb.ftp.sftp.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.ConfigurableService;
import software.tnb.common.utils.WaitUtils;
import software.tnb.ftp.common.FileTransferService;
import software.tnb.ftp.sftp.account.SFTPAccount;
import software.tnb.ftp.sftp.service.configuration.SFTPConfiguration;
import software.tnb.ftp.sftp.validation.SFTPValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.schmizz.sshj.sftp.SFTPClient;

public abstract class SFTP extends ConfigurableService<SFTPAccount, SFTPClient, SFTPValidation, SFTPConfiguration>
    implements FileTransferService, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(SFTP.class);

    protected final ExecutorService executor = Executors.newFixedThreadPool(1);

    protected abstract SFTPClient makeClient();

    @Override
    public int port() {
        return 2222;
    }

    @Override
    public SFTPClient client() {
        if (client == null) {
            LOG.debug("Creating new SFTPClient instance");
            client = makeClient();

            // Create a heartbeat that keeps the session alive, otherwise it would timeout after 10 minutes
            executor.submit((Runnable) () -> {
                while (true) {
                    try {
                        LOG.trace("SFTP client heartbeat");
                        client.ls("/");
                    } catch (Exception ignored) { }
                    WaitUtils.sleep(300000L);
                }
            });
        }
        return client;
    }

    @Override
    public SFTPValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new Ftp validation");
            validation = new SFTPValidation(account(), client());
        }
        return validation;
    }

    public Map<String, String> containerEnvironment() {
        Map<String, String> env = new HashMap<>();
        env.put("SFTP_SERVER_PORT", String.valueOf(port()));
        env.put("SFTP_SERVER_USERNAME", account().username());
        env.put("SFTP_SERVER_PASSWORD", account().password());
        env.put("SFTP_SERVER_HOME", account().homeDir());

        // Add TrustedUserCAKeys if configured
        if (account().trustedUserCAKeys() != null && !account().trustedUserCAKeys().isEmpty()) {
            env.put("SFTP_TRUSTED_USER_CA_KEYS", account().trustedUserCAKeys());
        }

        return env;
    }

    public String defaultImage() {
        return "quay.io/rh_integration/sftp-openssh-server:8.0";
    }

    @Override
    protected void defaultConfiguration() {
        // By default, certificate authentication is disabled
        getConfiguration().enableCertificateAuth(false);
    }
}
