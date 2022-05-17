package org.jboss.fuse.tnb.amq.account;

import org.jboss.fuse.tnb.common.account.Account;

public class AMQBrokerAccount implements Account {
    private String username = "admin";
    private String password = "admin123.";
    private String keystorePassword = "changeme";
    private String truststorePassword = "changeme";

    public String keystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public String truststorePassword() {
        return truststorePassword;
    }

    public void setTruststorePassword(String truststorePassword) {
        this.truststorePassword = truststorePassword;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }
}
