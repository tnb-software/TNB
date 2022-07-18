package software.tnb.jms.listener;

import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.common.MqttMessage;

public class MQTT5MessageListener extends MessageListener<MqttMessage> implements IMqttMessageListener {
    @Override
    public void messageArrived(String topic, MqttMessage message) {
        messages.add(message);
    }
}

