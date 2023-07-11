package software.tnb.jms.rabbitmq.validation;

import software.tnb.common.validation.Validation;
import software.tnb.jms.client.JMSClientManager;
import software.tnb.jms.rabbitmq.account.RabbitmqAccount;

import jakarta.jms.Connection;

public class RabbitMQValidation implements Validation {
    private final Connection connection;
    private final RabbitmqAccount account;
    private final String serverUrl;
    private JMSClientManager client;

    public RabbitMQValidation(Connection connection, RabbitmqAccount account, String serverUrl) {
        this.connection = connection;
        this.account = account;
        this.serverUrl = serverUrl;
    }

    private JMSClientManager client() {
        if (client == null) {
            client = new JMSClientManager(connection);
        }
        return client;
    }

    public String hostname() {
        String prefix = "tcp://";
        return serverUrl.substring(
            prefix.length(),
            serverUrl.lastIndexOf(':')
        );
    }
}
