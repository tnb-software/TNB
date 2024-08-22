package software.tnb.mail.validation.model;

import java.util.List;
import java.util.Map;

public record Email(List<String> from, List<String> to, String subject, String body, Map<String, byte[]> attachments, boolean unread) {
    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%b", String.join(",", from()), String.join(",", to()), subject.trim(), body.trim(), unread);
    }
}
