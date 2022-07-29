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
        return 1025;
    }

    public int httpPort() {
        return 8025;
    }

    public String defaultImage() {
        return "quay.io/fuse_qe/mailhog:v1.0.1";
    }

    public abstract String hostname();
}
