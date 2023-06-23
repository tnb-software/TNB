package software.tnb.mail.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MailValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(MailValidation.class);

    private final Map<String, String> accounts = new HashMap<>();
    private final String smtpHostname;
    private final String httpHostname;

    public MailValidation(String smtpHostname, String httpHostname) {
        this.smtpHostname = smtpHostname;
        this.httpHostname = httpHostname;
    }

    public void sendEmail(String subject, String message, String sender, String receiver) {
        if (!accounts.containsKey(sender)) {
            throw new IllegalArgumentException("Unable to find account " + sender + "! You first need to create the account using createUser method");
        }
        String[] parts = smtpHostname.split(":");
        try {
            Authenticator auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(sender, accounts.get(sender));
                }
            };

            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", parts[0]);
            properties.put("mail.smtp.port", Integer.parseInt(parts[1]));

            MimeMessage mimeMessage = new MimeMessage(Session.getInstance(properties, auth));

            mimeMessage.setFrom(sender);
            mimeMessage.setRecipients(Message.RecipientType.TO, receiver);
            mimeMessage.setText(message);
            mimeMessage.setSubject(subject);

            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This implementation is specific to James which expose a WebAdmin API
     *
     * @param username username
     * @return
     */
    public int countUnreadReadEmails(String username) {
        HTTPUtils.Response response = HTTPUtils.getInstance(HTTPUtils.trustAllSslClient())
            .get(String.format("http://%s/users/%s/mailboxes/INBOX/messageCount", httpHostname, username));

        return Integer.parseInt(response.getBody());
    }

    /**
     * This implementation is specific to James which expose a WebAdmin API
     *
     * @param username
     * @return
     */
    public String listMailboxes(String username) {
        HTTPUtils.Response response = HTTPUtils.getInstance(HTTPUtils.trustAllSslClient())
            .get(String.format("http://%s/users/%s/mailboxes", httpHostname, username));

        return response.getBody();
    }

    public void createUser(String username, String password) {
        HTTPUtils.Response response = HTTPUtils.getInstance(HTTPUtils.trustAllSslClient())
            .put(String.format("http://%s/users/%s?force", httpHostname, username),
                RequestBody.create(MediaType.get("application/json"), "{\"password\":\"" + password + "\"}"));

        if (response.getResponseCode() == 204) {
            accounts.put(username, password);
            LOG.info("User {} created", username);
        } else {
            throw new IllegalArgumentException("The username " + username + " is not valid, due to: " + response.getBody());
        }
    }
}
