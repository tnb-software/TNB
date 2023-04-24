package software.tnb.google.mail.validation;

import software.tnb.common.validation.Validation;
import software.tnb.google.mail.validation.model.GoogleEmail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleMailValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleMailValidation.class);

    private final Gmail client;

    public GoogleMailValidation(Gmail client) {
        this.client = client;
    }

    public List<GoogleEmail> getEmails(String query) {
        List<GoogleEmail> emails = new ArrayList<>();
        try {
            List<Message> messages = new ArrayList<>();
            ListMessagesResponse response = null;
            do {
                response = client.users().messages().list("me").setQ(query)
                    .setPageToken(response == null ? null : response.getNextPageToken()).execute();
                if (response.getMessages() != null) {
                    messages.addAll(response.getMessages());
                }
            } while (response.getNextPageToken() != null);

            for (Message message : messages) {
                LOG.trace("Fetching email with id {}", message.getId());
                emails.add(GoogleEmail.fromPayload(client.users().messages().get("me", message.getId()).execute()));
            }

            return emails;
        } catch (IOException e) {
            throw new RuntimeException("Unable to query emails:", e);
        }
    }
}
