package software.tnb.jms.rabbitmq.validation;

import software.tnb.common.validation.Validation;
import software.tnb.jms.client.JMSClientManager;
import software.tnb.jms.rabbitmq.account.RabbitmqAccount;

import javax.jms.Connection;

public class RabbitMQValidation implements Validation {
    private final Connection connection;
    private JMSClientManager client;
    private final RabbitmqAccount account;
    private final String mqttUrl;

    public RabbitMQValidation(Connection connection, RabbitmqAccount account, String mqttUrl) {
        this.connection = connection;
        this.account = account;
        this.mqttUrl = mqttUrl;
    }

    private JMSClientManager client() {
        if (client == null) {
            client = new JMSClientManager(connection);
        }
        return client;
    }
}
