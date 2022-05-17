package org.jboss.fuse.tnb.jms.listener;

import org.jboss.fuse.tnb.common.utils.WaitUtils;

import javax.jms.Message;
import javax.jms.MessageListener;

import java.util.ArrayList;
import java.util.List;

public class JMSMessageListener implements MessageListener {
    private final List<Message> messages = new ArrayList<>();
    private int index = 0;
    private boolean isSubscribed = false;

    @Override
    public void onMessage(Message message) {
        messages.add(message);
    }

    public Message next(long timeout) {
        checkSubscribe();
        WaitUtils.waitFor(() -> index < messages.size(), 30, timeout / 30, "Waiting until a next message arrives");
        return messages.get(index++);
    }

    public List<Message> getMessages() {
        checkSubscribe();
        return messages;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }

    private void checkSubscribe() {
        if (!isSubscribed) {
            throw new IllegalStateException("Message listener isn't subscribed to any topic! You need to call subscribe() first");
        }
    }
}
