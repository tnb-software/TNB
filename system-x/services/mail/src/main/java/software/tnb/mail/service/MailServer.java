package software.tnb.mail.service;

import software.tnb.common.account.Accounts;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.mail.account.MailAccount;
import software.tnb.mail.validation.MailValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MailServer implements Service, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(MailServer.class);

    private MailAccount account;
    private MailValidation validation;

    public MailAccount account() {
        if (account == null) {
            account = Accounts.get(MailAccount.class);
        }
        return account;
    }

    public MailValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new Mail validation");
            validation = new MailValidation(account(), hostname(), smtpPort(), httpPort());
        }
        return validation;
    }

    public int smtpPort() {
        return 25;
    }

    public int httpPort() {
        return 8000;
    }

    public int imapPort() {
        return 143;
    }

    public int popPort() {
        return 110;
    }

    public String defaultImage() {
        return "quay.io/fuse_qe/james-tnb:memory-3.7.0";
    }

    public abstract String hostname();
}
