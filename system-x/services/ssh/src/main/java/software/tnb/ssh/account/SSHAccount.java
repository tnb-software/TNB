package software.tnb.ssh.account;

import software.tnb.common.account.Account;

public class SSHAccount implements Account {

    private String username = "redhat";
    private String password = "redhat";

    public void setUsername(String username) {
        this.username = username;
    }

    public String username() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String password() {
        return password;
    }
}
