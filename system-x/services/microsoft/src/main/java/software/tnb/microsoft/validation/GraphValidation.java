package software.tnb.microsoft.validation;

import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Recipient;
import com.microsoft.graph.models.UserSendMailParameterSet;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;

import java.util.LinkedList;

import okhttp3.Request;

public class GraphValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(GraphValidation.class);

    private final GraphServiceClient<Request> client;

    public GraphValidation(GraphServiceClient<Request> client) {
        this.client = client;
    }

    public void sendEmail(String subject, String content, String from, String to) {
        Message message = new Message();
        message.subject = subject;
        ItemBody body = new ItemBody();
        body.contentType = BodyType.TEXT;
        body.content = content;
        message.body = body;

        LinkedList<Recipient> toRecipientsList = new LinkedList<>();
        Recipient toRecipients = new Recipient();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.address = to;
        toRecipients.emailAddress = emailAddress;
        toRecipientsList.add(toRecipients);
        message.toRecipients = toRecipientsList;

        LOG.debug("Sending email to " + to);
        try {
            client.users(from)
                .sendMail(UserSendMailParameterSet
                    .newBuilder()
                    .withMessage(message)
                    .withSaveToSentItems(false)
                    .build())
                .buildRequest()
                .post();
        } catch (ClientException e) {
            throw new RuntimeException("Failed to send email " + e);
        }
    }

    public void deleteEmail(String subject, String content, String from) {
        try {
            String idToDelete;

            MessageCollectionPage messagesPage = client.users(from).messages()
                .buildRequest()
                .top(1).get();

            final Message message = messagesPage.getCurrentPage().get(0);
            idToDelete = message.id;

            if (message.sender.emailAddress.address.equals(from) && message.subject.equals(subject) && message.bodyPreview.equals(content)) {
                LOG.debug(String.format("Deleting email from %s, with subject %s", from, subject));
                client.users(from).messages(idToDelete)
                    .buildRequest()
                    .delete();
            }
        } catch (ClientException e) {
            throw new RuntimeException("Failed to delete email " + e);
        }
    }
}
