package org.jboss.fuse.tnb.amq.validation;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MQTTValidation {

    private final IMqttClient mqttClient;

    Map<String, List<String>> messagesReceivedByTopic = new ConcurrentHashMap<>();

    private static MQTTValidation instance;

    public static MQTTValidation getInstance(IMqttClient mqttClient) {
        if (instance == null) {
            synchronized (MQTTValidation.class) {
                if (instance == null) {
                    instance = new MQTTValidation(mqttClient);
                }
            }
        }

        return instance;
    }

    private MQTTValidation(IMqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public void startListenForMessages(String topicFilter) throws MqttException {
        if (!messagesReceivedByTopic.containsKey(topicFilter)) {
            messagesReceivedByTopic.put(topicFilter, new ArrayList<>());
        }

        mqttClient.subscribe(topicFilter, (topic, msg) -> {
            byte[] payload = msg.getPayload();
            messagesReceivedByTopic.get(topicFilter).add(new String(payload));
        });
    }

    public List<String> waitForMessage(long timeout, String topicFilter) throws InterruptedException, MqttException {
        Thread.sleep(timeout);

        mqttClient.unsubscribe(topicFilter);
        return messagesReceivedByTopic.get(topicFilter);
    }

    public void sendMessage(String message, String topic) throws MqttException {
        mqttClient.publish(topic, new MqttMessage(message.getBytes()));
    }
}
