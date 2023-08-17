package software.tnb.ldap.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class LDAPConfiguration extends ServiceConfiguration {
    private static final String USE_REMOTE_SERVER = "ldap.use.remote.server";

    public LDAPConfiguration useRemoteUrl(boolean value) {
        set(USE_REMOTE_SERVER, value);
        return this;
    }

    public boolean isRemoteUrl() {
        return get(USE_REMOTE_SERVER, Boolean.class);
    }
}
