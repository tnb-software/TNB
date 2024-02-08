package software.tnb.microsoft.validation;

import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Recipient;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody;

import java.util.LinkedList;
import java.util.List;

public class GraphValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(GraphValidation.class);

    private final GraphServiceClient client;

    public GraphValidation(GraphServiceClient client) {
        this.client = client;
    }

    public void sendEmail(String subject, String content, String from, String to) {

        Message message = new Message();
        message.setSubject(subject);
        ItemBody body = new ItemBody();
        body.setContentType(BodyType.Text);
        body.setContent(content);
        message.setBody(body);

        LinkedList<Recipient> toRecipientsList = new LinkedList<>();
        Recipient toRecipients = new Recipient();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setAddress(to);
        toRecipients.setEmailAddress(emailAddress);
        toRecipientsList.add(toRecipients);
        message.setToRecipients(toRecipientsList);

        SendMailPostRequestBody sendMailPostRequestBody = new SendMailPostRequestBody();
        sendMailPostRequestBody.setMessage(message);
        sendMailPostRequestBody.setSaveToSentItems(true);
        LOG.debug("Sending email to " + to);
        client.users().byUserId(from).sendMail().post(sendMailPostRequestBody);
    }

    public void deleteEmail(String subject, String content, String from) {
        final List<Message> value = client.users().byUserId(from).messages().get().getValue();
        Message top = value.get(0);

        if ("from".equals(top.getSender().getEmailAddress().getAddress()) && subject.equals(top.getSubject())
            && top.getBodyPreview().equals(content)) {
            client.users().byUserId(from).messages().byMessageId(top.getId()).delete();
        }
    }
}
