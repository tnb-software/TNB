package software.tnb.mail.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.validation.Validation;
import software.tnb.mail.validation.model.Email;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MailValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(MailValidation.class);

    private final Map<String, String> accounts = new HashMap<>();
    private final String smtpHostname;
    private final String httpHostname;
    private final String imapHostname;

    public MailValidation(String smtpHostname, String httpHostname, String imapHostname) {
        this.smtpHostname = smtpHostname;
        this.httpHostname = httpHostname;
        this.imapHostname = imapHostname;
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
            properties.put("mail.smtp.port", parts[1]);

            MimeMessage mimeMessage = new MimeMessage(Session.getInstance(properties, auth));

            mimeMessage.setFrom(sender);
            mimeMessage.setRecipients(Message.RecipientType.TO, receiver);
            mimeMessage.setText(message);
            mimeMessage.setSubject(subject);

            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            LOG.error("Unable to send message", e);
            throw new RuntimeException(e);
        }
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

    public List<Email> getEmails(String user, String password) {
        List<Email> emails = new ArrayList<>();

        String[] parts = imapHostname.split(":");

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imap.host", parts[0]);
        properties.put("mail.imap.port", parts[1]);
        properties.put("mail.imap.ssl.enable", "false");

        Session session = Session.getInstance(properties);
        try {
            Store store = session.getStore();
            store.connect(user, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] msgs = inbox.getMessages();

            for (Message m : msgs) {
                boolean seen = m.isSet(Flags.Flag.SEEN);
                Email email = new Email(m.getFrom(), m.getAllRecipients(), m.getSubject(), m.getContent().toString(), !seen);

                if (m.isMimeType("multipart/*")) {
                    MimeMultipart multipart = ((MimeMultipart) m.getContent());
                    for (int i = 0; i < multipart.getCount(); i++) {
                        if (Part.ATTACHMENT.equalsIgnoreCase(multipart.getBodyPart(i).getDisposition())) {
                            BodyPart attachmentBody = multipart.getBodyPart(i);
                            // @formatter:off
                            final String fileName = Arrays.stream(attachmentBody.getContentType()
                                    .split(";"))
                                    .filter(part -> part.trim().startsWith("name="))
                                    .findFirst()
                                    .map(part -> part.split("=")[1].trim())
                                    .get();
                            // @formatter:on
                            email.attachments().put(fileName, IOUtils.toByteArray(attachmentBody.getInputStream()));
                        }
                    }
                }

                emails.add(email);
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            LOG.error("Unable to read emails", e);
        }

        return emails;
    }
}
