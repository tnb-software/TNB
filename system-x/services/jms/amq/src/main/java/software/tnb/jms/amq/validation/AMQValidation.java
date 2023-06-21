package software.tnb.jms.amq.validation;

import software.tnb.common.validation.Validation;
import software.tnb.jms.amq.account.AMQBrokerAccount;
import software.tnb.jms.client.JMSClientManager;
import software.tnb.jms.client.JMSQueueClient;
import software.tnb.jms.client.JMSTopicClient;
import software.tnb.jms.client.MQTT5TopicClient;
import software.tnb.jms.client.MQTTTopicClient;

import java.util.UUID;

import jakarta.jms.Connection;

public class AMQValidation implements Validation {
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

    public MQTT5TopicClient mqtt5(String topic) {
        return mqtt5(topic, UUID.randomUUID().toString());
    }

    public MQTTTopicClient mqtt(String topic, String clientId) {
        return client().mqtt(mqttUrl, account.username(), account.password(), clientId, topic);
    }

    public MQTT5TopicClient mqtt5(String topic, String clientId) {
        return client().mqtt5(mqttUrl, account.username(), account.password(), clientId, topic);
    }
}
