package software.tnb.jms.amq.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.jms.amq.service.AMQBroker;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import javax.jms.Connection;
import javax.jms.JMSException;

import java.util.HashMap;
import java.util.Map;

@AutoService(AMQBroker.class)
public class LocalAMQBroker extends AMQBroker implements Deployable, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(LocalAMQBroker.class);
    private AMQBrokerContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting AMQ container");
        container = new AMQBrokerContainer(image(), containerEnvironment(), containerPorts());
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
        client = createConnection();
    }

    @Override
    public void closeResources() {
        validation = null;
        try {
            client.close();
        } catch (JMSException e) {
            throw new RuntimeException("Can't close JMS connection", e);
        }
    }

    @Override
    public String brokerUrl() {
        return container.getHost();
    }

    @Override
    protected String mqttUrl() {
        return String.format("tcp://%s:%d", brokerUrl(), getPortMapping(1883));
    }

    @Override
    public int getPortMapping(int port) {
        return container.getMappedPort(port);
    }

    private int[] containerPorts() {
        return new int[] {8161, 61616, 1883};
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

    @Override
    public String defaultImage() {
        return "registry.redhat.io/amq7/amq-broker-rhel8:7.11.0";
    }
}
