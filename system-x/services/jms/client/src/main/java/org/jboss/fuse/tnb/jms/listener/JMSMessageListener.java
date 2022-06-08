package org.jboss.fuse.tnb.jms.listener;

import javax.jms.Message;
import javax.jms.MessageListener;

public class JMSMessageListener extends org.jboss.fuse.tnb.jms.listener.MessageListener<Message> implements MessageListener {

    @Override
    public void onMessage(Message message) {
        messages.add(message);
    }
}
