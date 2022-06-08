package org.jboss.fuse.tnb.jms.listener;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTMessageListener extends MessageListener<MqttMessage> implements IMqttMessageListener {
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        messages.add(message);
    }
}
