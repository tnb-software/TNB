package software.tnb.ldap.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.ldap.service.LDAPLocalStack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;

@AutoService(LDAPLocalStack.class)
public class LocalLDAP extends LDAPLocalStack implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalLDAP.class);
    private LDAPContainer ldapContainer;

    @Override
    public void deploy() {
        LOG.info("Starting LDAP container");
        ldapContainer = new LDAPContainer(defaultImage(), PORT, environmentVariables());
        ldapContainer.start();
        LOG.info("LDAP container started");
    }

    @Override
    public void undeploy() {
        if (ldapContainer != null) {
            LOG.info("Stopping LDAP container");
            ldapContainer.stop();
        }
    }

    @Override
    public String url() {
        return String.format("ldap://%s:%s", ldapContainer.getHost(), ldapContainer.getMappedPort(PORT));
    }

    @Override
    public void openResources() {
        final LDAPConnection ldapConnection = new LDAPConnection();
        try {
            ldapConnection.connect(ldapContainer.getHost(), ldapContainer.getMappedPort(PORT), 20000);
            ldapConnection.bind(account().getUsername(), account().getPassword());
            client = new LDAPConnectionPool(ldapConnection, 1);
        } catch (LDAPException e) {
            LOG.error("Error when connecting to LDAP server: " + e.getMessage());
            throw new RuntimeException("Error when connecting to LDAP server", e);
        }
    }

    @Override
    public void closeResources() {
        if (client != null) {
            client.close();
        }
    }
}
