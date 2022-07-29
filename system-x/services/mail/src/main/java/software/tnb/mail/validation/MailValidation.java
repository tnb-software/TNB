package software.tnb.mail.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.mail.account.MailAccount;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailValidation {
    private static final Logger LOG = LoggerFactory.getLogger(MailValidation.class);

    private final MailAccount account;
    private final String host;
    private final int smtpPort;
    private final int httpPort;

    public MailValidation(MailAccount account, String host, int smtpPort, int httpPort) {
        this.account = account;
        this.host = host;
        this.smtpPort = smtpPort;
        this.httpPort = httpPort;
    }

    public void sendEmail(String subject, String message) {
        try {
            Email email = new SimpleEmail();
            email.setHostName(host);
            email.setSmtpPort(smtpPort);
            email.setAuthenticator(new DefaultAuthenticator(account.username(), account.password()));
            email.setFrom(account.username());
            email.setSubject(subject);
            email.setMsg(message);
            email.addTo(account.recipient());
            email.send();
        } catch (EmailException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * The implementation is specific to MailHog, which expose a REST API
     */
    public String readEmails() {
        HTTPUtils.Response response = HTTPUtils.getInstance(HTTPUtils.trustAllSslClient())
            .get(String.format("http://%s:%d/api/v2/messages", host, httpPort));

        return response.getBody();
    }
}
