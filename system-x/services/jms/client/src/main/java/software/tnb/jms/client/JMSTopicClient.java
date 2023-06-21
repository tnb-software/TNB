package software.tnb.jms.client;

import software.tnb.jms.DestinationType;
import software.tnb.jms.listener.JMSMessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;

public class JMSTopicClient extends JMSClient implements TopicClient {
    private static final Logger LOG = LoggerFactory.getLogger(JMSTopicClient.class);

    private final JMSMessageListener listener = new JMSMessageListener();
    private MessageConsumer consumer;

    public JMSTopicClient(Session session, String topicName) {
        super(session, DestinationType.TOPIC, topicName);
    }

    @Override
    public void subscribe() {
        try {
            consumer = session.createConsumer(destination);
            consumer.setMessageListener(listener);
            listener.setSubscribed(true);
        } catch (JMSException e) {
            throw new RuntimeException("Unable to subscribe to topic " + destinationName, e);
        }
    }

    @Override
    public Message receiveMessage(long timeout) {
        return listener.next(timeout);
    }

    public List<String> receiveAll() {
        return listener.getMessages().stream().map(this::getBody).collect(Collectors.toList());
    }

    public void close() {
        try {
            producer.close();
            consumer.close();
            session.close();
        } catch (Exception e) {
            LOG.warn("Unable to close topic client", e);
        }
    }
}
