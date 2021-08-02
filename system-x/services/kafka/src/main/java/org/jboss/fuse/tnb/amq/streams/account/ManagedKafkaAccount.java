package org.jboss.fuse.tnb.amq.streams.account;

import org.jboss.fuse.tnb.common.account.Account;
import org.jboss.fuse.tnb.common.account.WithId;

public class ManagedKafkaAccount implements Account, WithId {
    private String clientID;
    private String clientSecret;

    @Override
    public String credentialsId() {
        return "kafka-rhoas-sa-tnb";
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
