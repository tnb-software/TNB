package software.tnb.mail.validation.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.mail.Address;

public class Email {
    private final List<String> from;
    private final List<String> to;
    private final String subject;
    private final String body;
    private final Map<String, byte[]> attachments = new HashMap<>();
    private final boolean unread;

    public Email(Address[] from, Address[] to, String subject, String body, boolean unread) {
        this.from = Arrays.stream(from).map(Address::toString).toList();
        this.to = Arrays.stream(to).map(Address::toString).toList();
        this.subject = subject;
        this.body = body;
        this.unread = unread;
    }

    public String from() {
        return String.join(",", from);
    }

    public String to() {
        return String.join(",", to);
    }

    public String subject() {
        return subject;
    }

    public String body() {
        return body;
    }

    public Map<String, byte[]> attachments() {
        return attachments;
    }

    public boolean isUnread() {
        return unread;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%b", from(), to(), subject.trim(), body.trim(), unread);
    }
}
