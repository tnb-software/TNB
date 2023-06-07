package software.tnb.jms.rabbitmq.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

import com.google.auto.service.AutoService;

import javax.jms.Connection;

import java.util.HashMap;
import java.util.Map;

import software.tnb.common.deployment.Deployable;
import software.tnb.common.deployment.WithDockerImage;

import software.tnb.jms.rabbitmq.service.RabbitMQ;

@AutoService(RabbitMQ.class)
public class RabbitMQBroker extends RabbitMQ implements Deployable, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(RabbitMQ.class);
    private RabbitMQBrokerContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting RabbitMQ container");
        container = new RabbitMQBrokerContainer(image(), containerEnvironment(), containerPorts());
        container.start();
        LOG.info("RabbitMQ broker container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping RabbitMQ broker container");
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
        } catch (Exception e) {
            throw new RuntimeException("Can't close JMS connection", e);
        }
    }

    @Override
    public String brokerUrl() {
        return container.getHost();
    }

    @Override
    protected String mqttUrl() {
        return String.format("tcp://%s:%d", brokerUrl(), getPortMapping(5672));
    }

    @Override
    public int getPortMapping(int port) {
        return container.getMappedPort(port);
    }

    private int[] containerPorts() {
        return new int[] {5672, 15672};
    }

    private Map<String, String> containerEnvironment() {
        final Map<String, String> env = new HashMap<>();
        env.put("RABBITMQ_USERNAME", account().username());
        env.put("RABBITMQ_PASSWORD", account().password());
        return env;
    }

    private Connection createConnection() {
        try {
            CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
            connectionFactory.setHost("localhost");
            connectionFactory.setPort(getPortMapping(5672));
            connectionFactory.setUsername(account().username());
            connectionFactory.setPassword(account().password());

            Connection connection = (Connection) connectionFactory.createConnection();
            connection.start();

            return connection;
        } catch (Exception e) {
            throw new RuntimeException("Can't create jms connection", e);
        }
    }

    @Override
    public String defaultImage() {
        return "docker.io/library/rabbitmq:3-management";
    }
}
