package org.jboss.fuse.tnb.sftp.account;

import org.jboss.fuse.tnb.common.FileTransferAccount;

public class SftpAccount implements FileTransferAccount {

    private String username = "test";
    private String password = "test";

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String username() {
        return username;
    }

    @Override
    public String password() {
        return password;
    }
    
    public String baseDir() {
        return "sftp";
    }
}
