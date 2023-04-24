package software.tnb.horreum.service;

import software.tnb.common.client.NoClient;
import software.tnb.common.service.Service;
import software.tnb.horreum.account.HorreumAccount;
import software.tnb.horreum.validation.HorreumValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

@AutoService(Horreum.class)
public class Horreum extends Service<HorreumAccount, NoClient, HorreumValidation> {
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        validation = new HorreumValidation(account());
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
    }
}
