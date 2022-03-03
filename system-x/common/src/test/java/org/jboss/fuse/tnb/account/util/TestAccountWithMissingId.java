package org.jboss.fuse.tnb.account.util;

import org.jboss.fuse.tnb.common.account.Account;
import org.jboss.fuse.tnb.common.account.WithId;

public class TestAccountWithMissingId implements Account, WithId {
    private String username;
    private String password;

    @Override
    public String credentialsId() {
        return "non-existent";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

