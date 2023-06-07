package software.tnb.ldap.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.ldap.account.LDAPAccount;
import software.tnb.ldap.validation.LDAPValidation;

import com.unboundid.ldap.sdk.LDAPConnectionPool;

public abstract class LDAP extends Service<LDAPAccount, LDAPConnectionPool, LDAPValidation> implements WithDockerImage {

    public abstract String url();

    public LDAPValidation validation() {
        if (validation == null) {
            validation = new LDAPValidation(client());
        }
        return validation;
    }

    @Override
    public String defaultImage() {
        return "quay.io/fuse_qe/openldap:1.5.0";
    }
}
