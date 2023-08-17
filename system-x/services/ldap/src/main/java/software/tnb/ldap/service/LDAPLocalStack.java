package software.tnb.ldap.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.ldap.account.LocalLDAPAccount;
import software.tnb.ldap.validation.LDAPValidation;

import org.apache.commons.lang3.StringUtils;

import com.unboundid.ldap.sdk.LDAPConnectionPool;

import java.util.Map;

public abstract class LDAPLocalStack extends Service<LocalLDAPAccount, LDAPConnectionPool, LDAPValidation> implements WithDockerImage {

    protected static final int PORT = 389;

    public abstract String url();

    public Map<String, String> environmentVariables() {
        return Map.of("OPENLDAP_ROOT_DN_SUFFIX"
            , StringUtils.substringAfter(account().getUsername(), ",")
            , "OPENLDAP_ROOT_DN_PREFIX"
            , StringUtils.substringBefore(account().getUsername(), ",")
            , "OPENLDAP_ROOT_PASSWORD"
            , account().getPassword()
        );
    }

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/ocp-openldap:latest";
    }
}
