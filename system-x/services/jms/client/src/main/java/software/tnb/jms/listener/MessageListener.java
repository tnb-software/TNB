package software.tnb.jms.listener;

import software.tnb.common.exception.TimeoutException;
import software.tnb.common.utils.WaitUtils;

import java.util.ArrayList;
import java.util.List;

public class MessageListener<T> {
    protected final List<T> messages = new ArrayList<>();
    private int index = 0;
    private boolean isSubscribed = false;

    public T next(long timeout) {
        checkSubscribe();
        try {
            WaitUtils.waitFor(() -> index < messages.size(), 30, timeout / 30, "Waiting until a next message arrives");
        } catch (TimeoutException e) {
            return null;
        }
        return messages.get(index++);
    }

    public List<T> getMessages() {
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
