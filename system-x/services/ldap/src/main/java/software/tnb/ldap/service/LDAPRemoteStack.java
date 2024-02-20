package software.tnb.ldap.service;

import software.tnb.common.service.Service;
import software.tnb.ldap.account.RemoteLDAPAccount;
import software.tnb.ldap.validation.LDAPValidation;

import com.unboundid.ldap.sdk.LDAPConnectionPool;

public abstract class LDAPRemoteStack extends Service<RemoteLDAPAccount, LDAPConnectionPool, LDAPValidation> {

    protected static final int PORT = 389;

    private boolean reachable;

    public abstract String url();

    public abstract LDAPConnectionPool getConnection();

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }
}
