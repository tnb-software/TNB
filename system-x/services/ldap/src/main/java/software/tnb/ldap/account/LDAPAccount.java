package software.tnb.ldap.account;

import software.tnb.common.account.Account;

public class LDAPAccount implements Account {

    private String username = "cn=admin,dc=example,dc=org";
    private String password = "admin";

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }
}
