package software.tnb.jms.client;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.jms.listener.MQTT5MessageListener;

import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;

public class MQTT5TopicClient implements BasicJMSOperations<MqttMessage>, TopicClient {
    protected final IMqttClient client;
    protected final String topicName;
    private final MQTT5MessageListener listener = new MQTT5MessageListener();

    public MQTT5TopicClient(String url, String username, String password, String clientId, String topicName) {
        try {
            this.topicName = topicName;
            client = new MqttClient(url, clientId);

            MqttConnectionOptions options = new MqttConnectionOptions();
            options.setAutomaticReconnect(true);
            options.setCleanStart(true);
            options.setConnectionTimeout(10);
            options.setUserName(username);
            options.setPassword(password.getBytes());
            if (url.startsWith("ssl://")) {
                options.setSocketFactory(HTTPUtils.trustAllSslClient().sslSocketFactory());
                options.setSSLHostnameVerifier((hostname, session) -> true);
                options.setHttpsHostnameVerificationEnabled(false);
            }

            client.connect(options);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create mqtt5 client instance", e);
        }
    }

    public void send(String message) {
        try {
            client.publish(topicName, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
            throw new RuntimeException("Unable to publish mqtt5 message", e);
        }
    }

    @Override
    public String receive(long timeout) {
        return new String(receiveMessage(timeout).getPayload());
    }

    public MqttMessage receiveMessage(long timeout) {
        return listener.next(timeout);
    }

    @Override
    public void subscribe() {
        try {
            MqttSubscription subscription = new MqttSubscription(topicName);
            MqttSubscription[] subscriptions = {subscription};
            IMqttMessageListener[] listeners = {listener};
            ((MqttClient) client).subscribe(subscriptions, listeners);
            listener.setSubscribed(true);
        } catch (MqttException e) {
            throw new RuntimeException("Unable to subscribe to mqtt5 topic", e);
        }
    }
}
