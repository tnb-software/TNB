package software.tnb.mail.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.mail.account.MailAccount;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.MediaType;
import okhttp3.RequestBody;

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

    /**
     * Send email FROM username1 TO username2
     *
     * @param subject
     * @param message
     */
    public void sendEmail(String subject, String message) {
        sendEmail(subject, message, account.getUsername1(), account.getPassword1(), account.getUsername2());
    }

    public void sendEmail(String subject, String message, String sender, String senderPassword, String receiver) {
        try {
            Email email = new SimpleEmail();
            email.setHostName(host);
            email.setSmtpPort(smtpPort);
            email.setAuthenticator(new DefaultAuthenticator(sender, senderPassword));
            email.setFrom(sender);
            email.setSubject(subject);
            email.setMsg(message);
            email.addTo(receiver);
            email.send();
        } catch (EmailException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * This implementation is specific to James which expose a WebAdmin API
     *
     * @param username
     * @return
     */
    public String countUnreadReadEmails(String username) {
        HTTPUtils.Response response = HTTPUtils.getInstance(HTTPUtils.trustAllSslClient())
            .get(String.format("http://%s:%d/users/%s/mailboxes/INBOX/messageCount", host, httpPort, username));

        return response.getBody();
    }

    /**
     * This implementation is specific to James which expose a WebAdmin API
     *
     * @param username
     * @return
     */
    public String listMailboxes(String username) {
        HTTPUtils.Response response = HTTPUtils.getInstance(HTTPUtils.trustAllSslClient())
            .get(String.format("http://%s:%d/users/%s/mailboxes", host, httpPort, username));

        return response.getBody();
    }

    public void createUser(String username, String password) {
        HTTPUtils.Response response = HTTPUtils.getInstance(HTTPUtils.trustAllSslClient())
            .put(String.format("http://%s:%d/users/%s?force", host, httpPort, username),
                RequestBody.create(MediaType.get("application/json"), "{\"password\":\"" + password + "\"}"));

        if (response.getResponseCode() == 204) {
            LOG.info("User {} created - {}", username, response.getBody());
        } else {
            throw new IllegalArgumentException("The username" + username + " is not valid, due to: " + response.getBody());
        }
    }
}
