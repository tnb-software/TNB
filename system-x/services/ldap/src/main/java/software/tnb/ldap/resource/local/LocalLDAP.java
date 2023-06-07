package software.tnb.ldap.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.ldap.service.LDAP;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;

@AutoService(LDAP.class)
public class LocalLDAP extends LDAP implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalLDAP.class);
    private static final int PORT = 389;
    private LDAPContainer ldapContainer;

    @Override
    public void deploy() {
        LOG.info("Starting LDAP container");
        ldapContainer = new LDAPContainer(defaultImage(), PORT);
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
            ldapConnection.connect(StringUtils.substringBetween(url(), "ldap://", ":"), Integer.parseInt(StringUtils.substringAfterLast(url(), ':')));
            ldapConnection.bind(account().username(), account().password());
            client = new LDAPConnectionPool(ldapConnection, 1);
        } catch (LDAPException e) {
            LOG.error("Error when connecting to LDAP server");
            throw new RuntimeException("Error when connecting to LDAP server");
        }
    }

    @Override
    public void closeResources() {
        client.close();
    }
}
