package software.tnb.certmanager.service;

import software.tnb.certmanager.validation.CertManagerValidation;
import software.tnb.common.account.NoAccount;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.service.Service;

import java.util.Optional;

import io.fabric8.certmanager.client.CertManagerClient;

public abstract class CertManager extends Service<NoAccount, CertManagerClient, CertManagerValidation> {

    @Override
    public CertManagerValidation validation() {
        validation = Optional.ofNullable(validation)
            .orElseGet(() -> new CertManagerValidation(client()));
        return validation;
    }

    @Override
    protected CertManagerClient client() {
        return OpenshiftClient.get().adapt(CertManagerClient.class);
    }
}
