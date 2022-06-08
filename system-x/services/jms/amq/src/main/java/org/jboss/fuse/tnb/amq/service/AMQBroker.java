package org.jboss.fuse.tnb.amq.service;

import org.jboss.fuse.tnb.amq.account.AMQBrokerAccount;
import org.jboss.fuse.tnb.amq.validation.AMQValidation;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;

import javax.jms.Connection;

public abstract class AMQBroker implements Service {
    protected Connection connection;

    protected AMQBrokerAccount account;
    private AMQValidation validation;

    public AMQBrokerAccount account() {
        if (account == null) {
            account = Accounts.get(AMQBrokerAccount.class);
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
