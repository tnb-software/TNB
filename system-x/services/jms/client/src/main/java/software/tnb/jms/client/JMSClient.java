package software.tnb.jms.client;

import software.tnb.jms.DestinationType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import jakarta.jms.BytesMessage;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

public abstract class JMSClient implements BasicJMSOperations<Message> {
    private static final Logger LOG = LoggerFactory.getLogger(JMSClient.class);

    protected final Session session;
    protected final Destination destination;
    protected final String destinationName;
    protected final MessageProducer producer;

    public JMSClient(Session session, DestinationType type, String destinationName) {
        this.session = session;
        this.destinationName = destinationName;
        try {
            switch (type) {
                case QUEUE:
                    destination = session.createQueue(destinationName);
                    break;
                case TOPIC:
                    destination = session.createTopic(destinationName);
                    break;
                default:
                    throw new IllegalArgumentException("Missing switch case implementation for a new destination type");
            }
            this.producer = session.createProducer(destination);
        } catch (JMSException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void send(String message) {
        send(message, null);
    }

    public void send(String message, Map<String, String> properties) {
        StringBuilder logMsg = new StringBuilder("Producing message ").append(message).append(" to destination ").append(destinationName);
        try {
            TextMessage textMessage = session.createTextMessage(message);
            if (properties != null) {
                logMsg.append(" with properties: ");
                properties.forEach((key, value) -> {
                    logMsg.append("[").append(key).append("=").append(value).append("] ");
                    try {
                        textMessage.setStringProperty(key, value);
                    } catch (JMSException e) {
                        throw new RuntimeException("Exception occurred during string property set.", e);
                    }
                });
            }
            LOG.debug(logMsg.substring(0, logMsg.length() - 2));
            producer.send(textMessage);
        } catch (JMSException e) {
            throw new RuntimeException("Unable to produce message:", e);
        }
    }

    public String receive() {
        return getBody(receiveMessage());
    }

    public String receive(long timeout) {
        return getBody(receiveMessage(timeout));
    }

    public Message receiveMessage() {
        return receiveMessage(30000L);
    }

    public abstract Message receiveMessage(long timeout);

    public String getBody(Message message) {
        if (message == null) {
            return null;
        }
        if (message instanceof TextMessage) {
            try {
                return ((TextMessage) message).getText();
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        } else if (message instanceof BytesMessage) {
            try {
                BytesMessage byteMessage = (BytesMessage) message;
                byte[] byteArr;
                byteArr = new byte[(int) byteMessage.getBodyLength()];
                byteMessage.readBytes(byteArr);
                byteMessage.reset();
                return new String(byteArr);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Not handled conversion to string for message class " + message.getClass());
        }
    }
}
