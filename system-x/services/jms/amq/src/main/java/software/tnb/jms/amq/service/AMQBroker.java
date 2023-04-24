package software.tnb.jms.amq.service;

import software.tnb.common.service.Service;
import software.tnb.jms.amq.account.AMQBrokerAccount;
import software.tnb.jms.amq.validation.AMQValidation;

import javax.jms.Connection;

public abstract class AMQBroker extends Service<AMQBrokerAccount, Connection, AMQValidation> {
    public abstract String brokerUrl();

    protected abstract String mqttUrl();

    public AMQValidation validation() {
        if (validation == null) {
            validation = new AMQValidation(client(), account(), mqttUrl());
        }
        return validation;
    }

    public abstract int getPortMapping(int port);
}
