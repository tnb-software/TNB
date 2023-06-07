package software.tnb.jms.rabbitmq.service;

import javax.jms.Connection;

import software.tnb.common.service.Service;
import software.tnb.jms.rabbitmq.account.RabbitmqAccount;
import software.tnb.jms.rabbitmq.validation.RabbitMQValidation;

public abstract class RabbitMQ extends Service<RabbitmqAccount, Connection, RabbitMQValidation> {
    public abstract String brokerUrl();

    protected abstract String mqttUrl();

    public RabbitMQValidation validation() {
        if (validation == null) {
            validation = new RabbitMQValidation(client(), account(), mqttUrl());
        }
        return validation;
    }

    public abstract int getPortMapping(int port);
}
