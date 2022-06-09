package software.tnb.ftp.sftp.service;

import software.tnb.ftp.common.FileTransferService;

import software.tnb.common.account.Accounts;
import software.tnb.ftp.sftp.account.SFTPAccount;
import software.tnb.ftp.sftp.validation.SFTPValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import net.schmizz.sshj.sftp.SFTPClient;

public abstract class SFTP implements FileTransferService {
    private static final Logger LOG = LoggerFactory.getLogger(SFTP.class);
    private static final String SFTP_IMAGE_KEY = "sftp.image";

    private SFTPAccount account;
    private SFTPValidation validation;

    public abstract SFTPClient client();

    @Override
    public int port() {
        return 22;
    }

    @Override
    public SFTPAccount account() {
        if (account == null) {
            account = Accounts.get(SFTPAccount.class);
        }
        return account;
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
            "SFTP_USERS", String.format("%s:%s:::%s", account().username(), account().password(), account().baseDir())
        );
    }

    public static String sftpImage() {
        return System.getProperty(SFTP_IMAGE_KEY, "quay.io/fuse_qe/sftp:alpine");
    }
}
