package org.jboss.fuse.tnb.amq.validation;

import org.jboss.fuse.tnb.amq.account.AMQBrokerAccount;
import org.jboss.fuse.tnb.jms.client.JMSClientManager;
import org.jboss.fuse.tnb.jms.client.JMSQueueClient;
import org.jboss.fuse.tnb.jms.client.JMSTopicClient;
import org.jboss.fuse.tnb.jms.client.MQTTTopicClient;

import javax.jms.Connection;

import java.util.UUID;

public class AMQValidation {
    private final Connection connection;
    private JMSClientManager client;
    private final AMQBrokerAccount account;
    private final String mqttUrl;

    public AMQValidation(Connection connection, AMQBrokerAccount account, String mqttUrl) {
        this.connection = connection;
        this.account = account;
        this.mqttUrl = mqttUrl;
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

    public MQTTTopicClient mqtt(String topic) {
        return mqtt(topic, UUID.randomUUID().toString());
    }

    public MQTTTopicClient mqtt(String topic, String clientId) {
        return client().mqtt(mqttUrl, account.username(), account.password(), clientId, topic);
    }
}
