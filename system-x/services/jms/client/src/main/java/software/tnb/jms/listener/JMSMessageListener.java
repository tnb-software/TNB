package software.tnb.jms.listener;

import jakarta.jms.Message;
import jakarta.jms.MessageListener;

public class JMSMessageListener extends software.tnb.jms.listener.MessageListener<Message> implements MessageListener {

    @Override
    public void onMessage(Message message) {
        messages.add(message);
    }
}
