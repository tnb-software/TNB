package software.tnb.ftp.sftp.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.ftp.common.FileTransferService;
import software.tnb.ftp.sftp.account.SFTPAccount;
import software.tnb.ftp.sftp.validation.SFTPValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import net.schmizz.sshj.sftp.SFTPClient;

public abstract class SFTP extends Service<SFTPAccount, SFTPClient, SFTPValidation> implements FileTransferService, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(SFTP.class);

    @Override
    public int port() {
        return 2222;
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
        return Map.of(
            "SFTP_SERVER_PORT", String.valueOf(port()),
            "SFTP_SERVER_USERNAME", account().username(),
            "SFTP_SERVER_PASSWORD", account().password(),
            "SFTP_SERVER_HOME", account().homeDir()
        );
    }

    public String defaultImage() {
        return "quay.io/rh_integration/sftp-server:latest";
    }
}
