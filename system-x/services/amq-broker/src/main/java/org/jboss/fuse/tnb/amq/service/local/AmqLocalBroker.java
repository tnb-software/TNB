package org.jboss.fuse.tnb.amq.service.local;

import org.jboss.fuse.tnb.amq.service.AmqBroker;
import org.jboss.fuse.tnb.common.deployment.Deployable;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import javax.jms.Connection;
import javax.jms.JMSException;

import java.util.HashMap;
import java.util.Map;

@AutoService(AmqBroker.class)
public class AmqLocalBroker extends AmqBroker implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(AmqLocalBroker.class);
    private AmqBrokerContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting AMQ container");
        container = new AmqBrokerContainer(containerEnvironment(), containerPorts());
        container.start();
        LOG.info("AMQ broker container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping AMQ broker container");
            container.stop();
        }
    }

    @Override
    public void openResources() {
        connection = createConnection();
    }

    @Override
    public void closeResources() {
        try {
            connection.close();
        } catch (JMSException e) {
            throw new RuntimeException("Can't close JMS connection");
        }
    }

    @Override
    public String brokerUrl() {
        return "localhost";
    }

    @Override
    public int getPortMapping(int port) {
        return container.getMappedPort(port);
    }

    private int[] containerPorts() {
        int[] ports = {8161, 61616};
        return ports;
    }

    private Map<String, String> containerEnvironment() {
        final Map<String, String> env = new HashMap<>();
        env.put("AMQ_USER", account().username());
        env.put("AMQ_PASSWORD", account().password());
        env.put("AMQ_ROLE", "admin");
        env.put("AMQ_NAME", "broker");
        env.put("AMQ_TRANSPORTS", "openwire,amqp,stomp,mqtt,hornetq");
        env.put("AMQ_REQUIRE_LOGIN", "true");
        return env;
    }

    private Connection createConnection() {
        try {

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(String
                .format("tcp://%s:%s", brokerUrl(), getPortMapping(61616)), account().username(), account().password());

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            return connection;
        } catch (JMSException e) {
            throw new RuntimeException("Can't create jms connection", e);
        }
    }
}
