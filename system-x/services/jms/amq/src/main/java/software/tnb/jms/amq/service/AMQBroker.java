package software.tnb.jms.amq.service;

import software.tnb.common.service.Service;
import software.tnb.jms.amq.account.AMQBrokerAccount;
import software.tnb.jms.amq.validation.AMQValidation;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import java.util.HashMap;
import java.util.Map;

import jakarta.jms.Connection;
import jakarta.jms.JMSException;

public abstract class AMQBroker extends Service<AMQBrokerAccount, Connection, AMQValidation> {
    public abstract String host();

    public abstract int getPortMapping(int port);

    public String openwireUrl() {
        return String.format("tcp://%s:%d", host(), getPortMapping(61616));
    }

    public String mqttUrl() {
        return String.format("tcp://%s:%d", host(), getPortMapping(1883));
    }

    protected String mqttClientUrl() {
        return mqttUrl();
    }

    public String amqpUrl() {
        return String.format("amqp://%s:%d", host(), getPortMapping(5672));
    }

    public AMQValidation validation() {
        if (validation == null) {
            validation = new AMQValidation(client(), account(), mqttClientUrl());
        }
        return validation;
    }

    public void openResources() {
        client = createConnection();
    }

    public void closeResources() {
        if (validation != null) {
            validation.close();
            validation = null;
        }
        try {
            client.close();
        } catch (JMSException e) {
            throw new RuntimeException("Can't close JMS connection", e);
        }
    }

    protected Connection createConnection() {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(openwireUrl(), account().username(), account().password());

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            return connection;
        } catch (JMSException e) {
            throw new RuntimeException("Can't create jms connection", e);
        }
    }

    protected Map<String, String> containerEnvironment() {
        final Map<String, String> env = new HashMap<>();
        env.put("AMQ_USER", account().username());
        env.put("AMQ_PASSWORD", account().password());
        env.put("AMQ_ROLE", "admin");
        env.put("AMQ_NAME", "broker");
        env.put("AMQ_TRANSPORTS", "openwire,amqp,stomp,mqtt,hornetq");
        env.put("AMQ_REQUIRE_LOGIN", "true");
        return env;
    }
}
