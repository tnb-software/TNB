package software.tnb.ldap.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.ConfigurableService;
import software.tnb.ldap.account.LDAPAccount;
import software.tnb.ldap.service.configuration.LDAPConfiguration;
import software.tnb.ldap.validation.LDAPValidation;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;

import java.util.Map;

public abstract class LDAP extends ConfigurableService<LDAPAccount, LDAPConnectionPool, LDAPValidation, LDAPConfiguration> implements
    WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(LDAP.class);
    protected static final int PORT = 389;
    protected boolean reachable = true;
    protected String host;
    protected int port;

    @Override
    protected void defaultConfiguration() {
        getConfiguration().useRemoteServer(false);
    }

    @Override
    public LDAPAccount account() {
        if (account == null) {
            if (getConfiguration().isRemoteServer()) {
                account = super.account();
            } else {
                account = new LDAPAccount();
            }
        }

        return account;
    }

    public String url() {
        return String.format("ldap://%s:%s", host, port);
    }

    public boolean isReachable() {
        return reachable;
    }

    public Map<String, String> environmentVariables() {
        return Map.of(
            "OPENLDAP_ROOT_DN_SUFFIX", StringUtils.substringAfter(account().username(), ","),
            "OPENLDAP_ROOT_DN_PREFIX", StringUtils.substringBefore(account().username(), ","),
            "OPENLDAP_ROOT_PASSWORD", account().password()
        );
    }

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/ocp-openldap:latest";
    }

    public LDAPValidation validation() {
        if (validation == null) {
            validation = new LDAPValidation(client());
        }
        return validation;
    }

    protected void initializeClient(LDAPAccount account) {
        final LDAPConnection ldapConnection = new LDAPConnection();

        if (getConfiguration().isRemoteServer()) {
            for (String remoteHost : account().host().split(",")) {
                try {
                    ldapConnection.connect(remoteHost, PORT, 20000);
                    ldapConnection.bind(account().username(), account().password());
                    client = new LDAPConnectionPool(ldapConnection, 1);
                    host = remoteHost;
                    reachable = true;
                    break;
                } catch (LDAPException e) {
                    LOG.error("Error when connecting to LDAP server: {}", e.getMessage());
                    reachable = false;
                }
            }
        } else {
            host = account.host();
            try {
                ldapConnection.connect(host, port, 20000);
                ldapConnection.bind(account().username(), account().password());
                client = new LDAPConnectionPool(ldapConnection, 1);
            } catch (LDAPException e) {
                LOG.error("Error when connecting to LDAP server: {}", e.getMessage());
                throw new RuntimeException("Error when connecting to LDAP server", e);
            }
        }

    }
}
