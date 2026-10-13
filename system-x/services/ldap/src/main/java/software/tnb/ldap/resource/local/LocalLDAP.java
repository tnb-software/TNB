package software.tnb.ldap.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.ldap.service.LDAP;

import com.google.auto.service.AutoService;
import com.unboundid.ldap.sdk.LDAPConnectionPool;

@AutoService(LDAP.class)
public class LocalLDAP extends LDAP implements ContainerDeployable<LDAPContainer> {
    private final LDAPContainer ldapContainer = new LDAPContainer(image(), PORT, environmentVariables());

    @Override
    public LDAPConnectionPool client() {
        return client;
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
        if (client != null) {
            client.close();
        }
    }

    @Override
    public LDAPContainer container() {
        return ldapContainer;
    }

    @Override
    public String url() {
        return String.format("ldap://%s:%d", host(), port());
    }

    @Override
    protected String host() {
        return container().getHost();
    }

    @Override
    protected int port() {
        return container().getMappedPort(PORT);
    }
}
