package org.jboss.fuse.tnb.ftp.account;

import org.jboss.fuse.tnb.common.FileTransferAccount;

public class FtpAccount implements FileTransferAccount {

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
}
