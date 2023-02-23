package software.tnb.microsoft.service;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.service.Service;
import software.tnb.microsoft.account.MicrosoftAccount;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(Exchange.class)
public class Exchange implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(Exchange.class);
    MicrosoftAccount account;

    public MicrosoftAccount account() {
        if (account == null) {
            LOG.debug("Creating new Exchange account");
            account = AccountFactory.create(MicrosoftAccount.class);
        }
        return account;
    }

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
