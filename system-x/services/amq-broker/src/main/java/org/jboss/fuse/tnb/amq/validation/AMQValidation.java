package org.jboss.fuse.tnb.amq.validation;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

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

    public void sendMessage(String message) {
        Session session = null;
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(getQueueName());
            MessageProducer producer = session.createProducer(destination);
            producer.send(session.createTextMessage(message));
        } catch (JMSException e) {
            throw new RuntimeException("The message was not sent.", e);
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    throw new RuntimeException("Can't close session.", e);
                }
            }
        }
    }

    public String waitForMessage(long timeout) {
        Session session = null;
        Message message = null;
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(getQueueName());
            MessageConsumer consumer = session.createConsumer(destination);
            message = consumer.receive(timeout);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    throw new RuntimeException("Can't close session.", e);
                }
            }
        }

        if (message instanceof TextMessage) {
            try {
                return ((TextMessage) message).getText();
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
