package software.tnb.jms.listener;

import javax.jms.Message;
import javax.jms.MessageListener;

public class JMSMessageListener extends software.tnb.jms.listener.MessageListener<Message> implements MessageListener {

    @Override
    public void onMessage(Message message) {
        messages.add(message);
    }
}
