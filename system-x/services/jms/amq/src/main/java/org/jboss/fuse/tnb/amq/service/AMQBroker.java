package org.jboss.fuse.tnb.amq.service;

import org.jboss.fuse.tnb.amq.account.AMQBrokerAccount;
import org.jboss.fuse.tnb.amq.validation.AMQValidation;
import org.jboss.fuse.tnb.amq.validation.MQTTValidation;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;

import org.eclipse.paho.client.mqttv3.IMqttClient;

import javax.jms.Connection;

public abstract class AMQBroker implements Service {
    protected Connection connection;

    protected AMQBrokerAccount account;
    private AMQValidation validation;

    protected IMqttClient mqttClient;

    public AMQBrokerAccount account() {
        if (account == null) {
            account = Accounts.get(AMQBrokerAccount.class);
        }
        return account;
    }

    public abstract String brokerUrl();

    public AMQValidation validation() {
        if (validation == null) {
            validation = new AMQValidation(connection);
        }
        return validation;
    }

    public MQTTValidation getMQTTValidation() {
        return MQTTValidation.getInstance(mqttClient);
    }

    public abstract int getPortMapping(int port);
}
