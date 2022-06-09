package software.tnb.salesforce.service;

import software.tnb.common.account.Accounts;
import software.tnb.common.service.Service;
import software.tnb.common.utils.WaitUtils;
import software.tnb.salesforce.account.SalesforceAccount;
import software.tnb.salesforce.validation.SalesforceValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.force.api.ApiConfig;
import com.force.api.ForceApi;
import com.google.auto.service.AutoService;

@AutoService(Salesforce.class)
public class Salesforce implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(Salesforce.class);

    private SalesforceAccount account;
    private ForceApi client;
    private SalesforceValidation validation;

    public SalesforceAccount account() {
        if (account == null) {
            account = Accounts.get(SalesforceAccount.class);
        }
        return account;
    }

    protected ForceApi client() {
        if (client == null) {
            WaitUtils.waitFor(() -> {
                try {
                    client = new ForceApi(new ApiConfig()
                        .setClientId(account.clientId())
                        .setClientSecret(account.clientSecret())
                        .setUsername(account.userName())
                        .setPassword(account.password())
                        .setForceURL(account.loginUrl()));
                    return true;
                } catch (Exception ex) {
                    LOG.error("Unable to connect to salesforce, will retry in 5 minutes");
                    return false;
                }
            }, 3, 300000L, "Unable to connect to SalesForce");
        }
        return client;
    }

    public SalesforceValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new Salesforce validation");
            validation = new SalesforceValidation(client(), account());
        }
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
    }
}
