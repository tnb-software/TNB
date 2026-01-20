package software.tnb.ldap.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.ldap.service.LDAP;

import com.google.auto.service.AutoService;
import com.unboundid.ldap.sdk.LDAPConnectionPool;

@AutoService(LDAP.class)
public class LocalLDAP extends LDAP implements ContainerDeployable<LDAPContainer> {
    private LDAPContainer ldapContainer;

    @Override
    public void deploy() {
        if (!getConfiguration().isRemoteServer()) {
            ldapContainer = new LDAPContainer(image(), PORT, environmentVariables());
            ContainerDeployable.super.deploy();
        }
    }

    @Override
    public void undeploy() {
        if (!getConfiguration().isRemoteServer()) {
            ContainerDeployable.super.undeploy();
        }
    }

    @Override
    public LDAPConnectionPool client() {
        return client;
    }

    @Override
    public void openResources() {
        port = getConfiguration().isRemoteServer() ? PORT : ldapContainer.getMappedPort(PORT);
        initializeClient(account());
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
}
