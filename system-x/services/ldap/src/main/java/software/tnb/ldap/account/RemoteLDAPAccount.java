package software.tnb.ldap.account;

import software.tnb.common.account.WithId;

public class RemoteLDAPAccount extends LDAPAccount implements WithId {
    private String username;
    private String password;
    private String host;

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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String credentialsId() {
        return "ldap";
    }
}
