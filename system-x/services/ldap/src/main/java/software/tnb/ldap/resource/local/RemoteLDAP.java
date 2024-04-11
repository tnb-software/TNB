package software.tnb.ldap.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.ldap.service.LDAPRemoteStack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;

@AutoService(LDAPRemoteStack.class)
public class RemoteLDAP extends LDAPRemoteStack implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteLDAP.class);

    private String firstReachableRemoteURL;

    @Override
    public void deploy() { }

    @Override
    public void undeploy() { }

    @Override
    public String url() {
        return firstReachableRemoteURL;
    }

    @Override
    public LDAPConnectionPool getConnection() {
        return client;
    }

    @Override
    public void openResources() {
        final LDAPConnection ldapConnection = new LDAPConnection();
        for (String remoteHost : account().getHost().split(",")) {
            try {
                ldapConnection.connect(remoteHost, PORT, 20000);
                ldapConnection.bind(account().getUsername(), account().getPassword());
                client = new LDAPConnectionPool(ldapConnection, 1);
                firstReachableRemoteURL = String.format("ldap://%s:%s", remoteHost, PORT);
                setReachable(true);
                break;
            } catch (LDAPException e) {
                LOG.error("Error when connecting to remote LDAP server: " + e.getMessage());
                setReachable(false);
            }
        }
    }

    @Override
    public void closeResources() {
        if (client != null) {
            client.close();
        }
    }
}
