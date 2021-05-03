package org.jboss.fuse.tnb.sftp.account;

import org.jboss.fuse.tnb.common.account.Account;

public class SftpAccount implements Account {

    private String username = "test";
    private String password = "test";

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
    
    public String baseDir() {
        return "sftp";
    }
}
