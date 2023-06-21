package software.tnb.jms.client;

import software.tnb.jms.DestinationType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;

public class JMSQueueClient extends JMSClient {
    private static final Logger LOG = LoggerFactory.getLogger(JMSQueueClient.class);

    public JMSQueueClient(Session session, String queueName) {
        super(session, DestinationType.QUEUE, queueName);
    }

    @Override
    public Message receiveMessage(long timeout) {
        try (MessageConsumer consumer = session.createConsumer(destination)) {
            return consumer.receive(timeout);
        } catch (JMSException e) {
            throw new RuntimeException("Unable to receive queue message:", e);
        }
    }

    public void close() {
        try {
            producer.close();
            session.close();
        } catch (Exception e) {
            LOG.warn("Unable to close producer/session:", e);
        }
    }
}
