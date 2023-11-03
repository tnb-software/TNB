package software.tnb.jms.rabbitmq.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.jms.rabbitmq.account.RabbitmqAccount;
import software.tnb.jms.rabbitmq.validation.RabbitMQValidation;

import java.util.HashMap;
import java.util.Map;

import jakarta.jms.Connection;

public abstract class RabbitMQ extends Service<RabbitmqAccount, Connection, RabbitMQValidation> implements WithDockerImage {
    public static final int PORT = 5672;
    public static final int MANAGEMENT_PORT = 15672;

    protected abstract String getServerUrl();

    public abstract int getPortMapping();

    public RabbitMQValidation validation() {
        if (validation == null) {
            validation = new RabbitMQValidation(client(), account(), getServerUrl());
        }
        return validation;
    }

    protected Map<String, String> containerEnvironment() {
        final Map<String, String> env = new HashMap<>();
        env.put("RABBITMQ_DEFAULT_USER", account().username());
        env.put("RABBITMQ_DEFAULT_PASS", account().password());
        return env;
    }

    @Override
    public String defaultImage() {
        return "quay.io/rh_integration/rabbitmq:3-management";
    }
}
