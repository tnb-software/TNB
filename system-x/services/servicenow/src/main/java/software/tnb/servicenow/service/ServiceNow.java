package software.tnb.servicenow.service;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.service.Service;
import software.tnb.servicenow.account.ServiceNowAccount;
import software.tnb.servicenow.validation.ServiceNowValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(ServiceNow.class)
public class ServiceNow implements Service {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceNow.class);

    private ServiceNowAccount account;
    private ServiceNowValidation validation;

    public ServiceNowAccount account() {
        if (account == null) {
            account = AccountFactory.create(ServiceNowAccount.class);
        }
        return account;
    }

    public ServiceNowValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new ServiceNow validation");
            validation = new ServiceNowValidation(account());
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
