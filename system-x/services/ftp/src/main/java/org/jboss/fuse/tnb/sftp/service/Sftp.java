package org.jboss.fuse.tnb.sftp.service;

import org.jboss.fuse.tnb.common.FileTransferService;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.sftp.account.SftpAccount;
import org.jboss.fuse.tnb.sftp.validation.SftpValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import net.schmizz.sshj.sftp.SFTPClient;

public abstract class Sftp implements FileTransferService {
    private static final Logger LOG = LoggerFactory.getLogger(Sftp.class);
    private static final String SFTP_IMAGE_KEY = "sftp.image";

    private SftpAccount account;
    private SftpValidation validation;

    public abstract SFTPClient client();

    @Override
    public int port() {
        return 22;
    }

    @Override
    public SftpAccount account() {
        if (account == null) {
            account = Accounts.get(SftpAccount.class);
        }
        return account;
    }

    @Override
    public SftpValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new Ftp validation");
            validation = new SftpValidation(account(), client());
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
