package org.jboss.fuse.tnb.account.util;

import org.jboss.fuse.tnb.common.account.Account;
import org.jboss.fuse.tnb.common.account.WithId;

public class TestAccountWithId implements Account, WithId {
    private String username;
    private String password;
    private int account_id;

    @Override
    public String credentialsId() {
        return "test-acc";
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

    public int accountId() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }
}
