package software.tnb.horreum.service;

import software.tnb.common.account.Accounts;
import software.tnb.common.service.Service;
import software.tnb.horreum.account.HorreumAccount;
import software.tnb.horreum.validation.HorreumValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

@AutoService(Horreum.class)
public class Horreum implements Service {

    private HorreumValidation validation;

    public HorreumValidation validation() {
        return validation;
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        validation = new HorreumValidation(Accounts.get(HorreumAccount.class));
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
    }
}
