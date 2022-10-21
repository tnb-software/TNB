package software.tnb.google.mail.validation.model;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GoogleEmail {
    private String from;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String body;

    public String from() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> to() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public List<String> cc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public List<String> bcc() {
        return bcc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    public String subject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String body() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public static GoogleEmail fromPayload(Message m) {
        GoogleEmail email = new GoogleEmail();

        final Map<String, String> headers = m.getPayload().getHeaders().stream()
                .collect(Collectors.toMap(MessagePartHeader::getName, MessagePartHeader::getValue, (k1, k2) -> k1));
        email.setFrom(headers.get("From"));
        email.setSubject(headers.get("Subject"));
        email.setTo(Arrays.stream(headers.get("To").split(",")).map(String::trim).collect(Collectors.toList()));
        if (headers.get("Cc") != null) {
            email.setCc(Arrays.stream(headers.get("Cc").split(",")).map(String::trim).collect(Collectors.toList()));
        }
        if (headers.get("Bcc") != null) {
            email.setBcc(Arrays.stream(headers.get("Bcc").split(",")).map(String::trim).collect(Collectors.toList()));
        }

        // If the body is short, it is present in payload.body
        // if it's long, the payload.body is null and the actual content is in payload.parts in a part with content type text/plain
        // there is also one other part with text/html content type, which contains the message body, but wrapped in some <div> element
        if (m.getPayload().getBody().getData() != null) {
            email.setBody(new String(Base64.getDecoder().decode(m.getPayload().getBody().getData())));
        } else if (m.getPayload().getParts() != null) {
            Optional<MessagePart> part = m.getPayload().getParts().stream().filter(mp -> "text/plain".equals(mp.getMimeType())).findFirst();
            if (part.isEmpty()) {
                throw new RuntimeException("Expected at least one text/plain message part in the message, but couldn't find any");
            }
            email.setBody(new String(part.get().getBody().decodeData()));
        }

        email.setBody(email.body().trim());

        return email;
    }
}
