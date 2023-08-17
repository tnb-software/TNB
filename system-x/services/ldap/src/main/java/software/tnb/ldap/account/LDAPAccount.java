package software.tnb.ldap.account;

import software.tnb.common.account.Account;

public abstract class LDAPAccount implements Account {
    public abstract String getUsername();

    public abstract String getPassword();

    public abstract String getHost();
}
