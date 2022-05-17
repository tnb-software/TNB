package org.jboss.fuse.tnb.amq.validation;

import org.jboss.fuse.tnb.jms.client.JMSClientManager;
import org.jboss.fuse.tnb.jms.client.JMSQueueClient;
import org.jboss.fuse.tnb.jms.client.JMSTopicClient;

import javax.jms.Connection;

public class AMQValidation {
    private final Connection connection;
    private JMSClientManager client;

    public AMQValidation(Connection connection) {
        this.connection = connection;
    }

    private JMSClientManager client() {
        if (client == null) {
            client = new JMSClientManager(connection);
        }
        return client;
    }

    public JMSQueueClient queue(String queue) {
        return client().queue(queue);
    }

    public JMSTopicClient topic(String topic) {
        return client().topic(topic);
    }
}
