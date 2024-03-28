package software.tnb.ssh.account;

import software.tnb.common.account.Account;

public class SshAccount implements Account {

    private String username = "redhat";

    public void setUsername(String username) {
        this.username = username;
    }

    public String username() {
        return username;
    }
}
