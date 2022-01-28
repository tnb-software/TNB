package org.jboss.fuse.tnb.amq.validation;

import org.apache.activemq.artemis.jms.client.ActiveMQBytesMessage;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import java.util.Map;

public class AMQValidation {

    private Connection connection;
    private String queueName = "TNB.TEST";

    public AMQValidation(Connection connection) {
        this.connection = connection;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    /**
     * If used in a camel route message and props are mapped to ${body} and ${headers.key} respectively
     *
     * @param message
     * @param props
     */
    public void sendMessage(String message, Map<String, String> props) {
        try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);) {
            Destination destination = session.createQueue(getQueueName());
            MessageProducer producer = session.createProducer(destination);
            TextMessage textMessage = session.createTextMessage(message);
            if (props != null) {
                props.forEach((key, value) -> {
                    try {
                        textMessage.setStringProperty(key, value);
                    } catch (JMSException e) {
                        throw new RuntimeException("Exception occurred during string property set.", e);
                    }
                });
            }
            producer.send(textMessage);
        } catch (JMSException e) {
            throw new RuntimeException("The message was not sent.", e);
        }
    }

    public void sendMessage(String message) {
        sendMessage(message, null);
    }

    public String waitForMessage(long timeout, String queue) {
        Message message = null;
        try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
            Destination destination = session.createQueue(queue);
            MessageConsumer consumer = session.createConsumer(destination);
            message = consumer.receive(timeout);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

        if (message instanceof TextMessage) {
            try {
                return ((TextMessage) message).getText();
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        } else if (message instanceof ActiveMQBytesMessage) {
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
        }
        return null;
    }

    public String waitForMessage(long timeout) {
        return waitForMessage(timeout, getQueueName());
    }
}
