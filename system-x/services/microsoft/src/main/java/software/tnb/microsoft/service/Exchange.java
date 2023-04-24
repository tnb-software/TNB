package software.tnb.microsoft.service;

import software.tnb.common.client.NoClient;
import software.tnb.common.service.Service;
import software.tnb.common.validation.NoValidation;
import software.tnb.microsoft.account.MicrosoftAccount;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(Exchange.class)
public class Exchange extends Service<MicrosoftAccount, NoClient, NoValidation> {

    public Map<String, String> getCredentials() {
        return Map.of(
            "exchange.user", account().username(),
            "exchange.clientId", account().clientId(),
            "exchange.clientSecret", account().clientSecret(),
            "exchange.tenantId", account().tenantId()
        );
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
    }
}
