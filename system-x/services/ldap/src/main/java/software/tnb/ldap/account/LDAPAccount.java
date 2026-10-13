package software.tnb.ldap.account;

import software.tnb.common.account.Account;

public class LDAPAccount implements Account {
    private String username = "cn=admin,dc=redhat,dc=com";
    private String password = "admin";
    private String host = "localhost";

    public String username() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String host() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
