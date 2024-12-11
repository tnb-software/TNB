package software.tnb.certmanager.service;

import software.tnb.certmanager.validation.CertManagerValidation;
import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.service.Service;

import java.util.Optional;

public abstract class CertManager extends Service<NoAccount, NoClient, CertManagerValidation> {

    @Override
    public CertManagerValidation validation() {
        validation = Optional.ofNullable(validation)
            .orElseGet(CertManagerValidation::new);
        return validation;
    }
}
