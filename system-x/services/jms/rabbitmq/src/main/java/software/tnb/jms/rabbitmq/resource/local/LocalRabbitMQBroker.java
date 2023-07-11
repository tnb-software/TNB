package software.tnb.jms.rabbitmq.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.jms.rabbitmq.service.RabbitMQ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;
import com.rabbitmq.jms.admin.RMQConnectionFactory;

import jakarta.jms.Connection;

@AutoService(RabbitMQ.class)
public class LocalRabbitMQBroker extends RabbitMQ implements Deployable, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(RabbitMQ.class);
    private RabbitMQBrokerContainer container;

    @Override
    public String defaultImage() {
        return IMAGE;
    }

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
    protected String getServerUrl() {
        return String.format("tcp://%s:%d", container.getHost(), getPortMapping());
    }

    @Override
    public int getPortMapping() {
        return container.getMappedPort(PORT);
    }

    private int[] containerPorts() {
        return new int[] {PORT, MANAGEMENT_PORT};
    }

    private Connection createConnection() {
        try {
            RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
            connectionFactory.setHost(container.getHost());
            connectionFactory.setPort(getPortMapping());
            connectionFactory.setUsername(account().username());
            connectionFactory.setPassword(account().password());

            Connection connection = connectionFactory.createConnection();
            connection.start();

            return connection;
        } catch (Exception e) {
            throw new RuntimeException("Can't create jms connection", e);
        }
    }
}
