package org.jboss.fuse.tnb.ftp.service;

import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.ftp.account.FtpAccount;
import org.jboss.fuse.tnb.ftp.validation.FtpValidation;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class Ftp implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(Ftp.class);

    private FtpAccount account;
    private FtpValidation validation;

    public abstract FTPClient client();

    public abstract int port();

    public abstract String host();

    public FtpAccount account() {
        if (account == null) {
            account = Accounts.get(FtpAccount.class);
        }
        return account;
    }

    public FtpValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new Ftp validation");
            validation = new FtpValidation(client());
        }
        return validation;
    }

    public Map<String, String> containerEnvironment() {
        return Map.of(
            "FTP_USERNAME", account().username(),
            "FTP_PASSWORD", account().password()
        );
    }

    protected String localClientHost() {
        return host();
    }
}
