package software.tnb.jms.client;

import java.util.HashMap;
import java.util.Map;

import jakarta.jms.Connection;
import jakarta.jms.JMSException;
import jakarta.jms.Session;

public class JMSClientManager {
    private final Connection connection;
    private final Map<String, JMSQueueClient> queueClients = new HashMap<>();
    private final Map<String, JMSTopicClient> topicClients = new HashMap<>();
    private final Map<String, MQTTTopicClient> mqttTopicClients = new HashMap<>();
    private final Map<String, MQTT5TopicClient> mqtt5TopicClients = new HashMap<>();

    public JMSClientManager(Connection connection) {
        this.connection = connection;
    }

    public JMSQueueClient queue(String queueName) {
        return queueClients.computeIfAbsent(queueName, v -> new JMSQueueClient(newSession(), queueName));
    }

    public JMSTopicClient topic(String topicName) {
        return topicClients.computeIfAbsent(topicName, v -> new JMSTopicClient(newSession(), topicName));
    }

    public MQTTTopicClient mqtt(String url, String username, String password, String clientId, String topicName) {
        return mqttTopicClients.computeIfAbsent(topicName, v -> new MQTTTopicClient(url, username, password, clientId, topicName));
    }

    public MQTT5TopicClient mqtt5(String url, String username, String password, String clientId, String topicName) {
        return mqtt5TopicClients.computeIfAbsent(topicName, v -> new MQTT5TopicClient(url, username, password, clientId, topicName));
    }

    private Session newSession() {
        try {
            return connection.createSession(Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            throw new RuntimeException("Unable to create new session:", e);
        }
    }

    public void close() {
        queueClients.values().forEach(JMSQueueClient::close);
        queueClients.clear();
        topicClients.values().forEach(JMSTopicClient::close);
        topicClients.clear();
        mqttTopicClients.values().forEach(MQTTTopicClient::close);
        mqtt5TopicClients.values().forEach(MQTT5TopicClient::close);
    }
}
