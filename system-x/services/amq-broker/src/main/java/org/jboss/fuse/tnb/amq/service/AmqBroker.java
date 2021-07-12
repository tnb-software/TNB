package org.jboss.fuse.tnb.amq.service;

import org.jboss.fuse.tnb.amq.account.AmqBrokerAccount;
import org.jboss.fuse.tnb.amq.validation.AMQValidation;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.service.Service;

import javax.jms.Connection;

public abstract class AmqBroker implements Service {
    protected Connection connection;

    protected AmqBrokerAccount account;

    public AmqBrokerAccount account() {
        if (account == null) {
            account = Accounts.get(AmqBrokerAccount.class);
        }
        return account;
    }

    public abstract String brokerUrl();

    public AMQValidation getValidation() {
        return new AMQValidation(connection);
    }

    public abstract int getPortMapping(int port);
}
