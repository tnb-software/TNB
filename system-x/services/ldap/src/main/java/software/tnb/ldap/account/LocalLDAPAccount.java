package software.tnb.ldap.account;

public class LocalLDAPAccount extends LDAPAccount {
    private String username = "cn=admin,dc=redhat,dc=com";
    private String password = "admin";
    private String host = "127.0.0.1";

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getHost() {
        return host;
    }
}
