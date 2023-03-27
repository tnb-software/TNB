package software.tnb.jms.amq.service;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.service.Service;
import software.tnb.jms.amq.account.AMQBrokerAccount;
import software.tnb.jms.amq.validation.AMQValidation;

import javax.jms.Connection;

public abstract class AMQBroker implements Service {
    protected Connection connection;

    protected AMQBrokerAccount account;
    protected AMQValidation validation;

    public AMQBrokerAccount account() {
        if (account == null) {
            account = AccountFactory.create(AMQBrokerAccount.class);
        }
        return account;
    }

    public abstract String brokerUrl();

    protected abstract String mqttUrl();

    public AMQValidation validation() {
        if (validation == null) {
            validation = new AMQValidation(connection, account(), mqttUrl());
        }
        return validation;
    }

    public abstract int getPortMapping(int port);
}
