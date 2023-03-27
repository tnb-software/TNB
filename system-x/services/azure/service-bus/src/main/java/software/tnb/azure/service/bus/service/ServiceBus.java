package software.tnb.azure.service.bus.service;

import software.tnb.azure.common.account.AzureServiceBusAccount;
import software.tnb.azure.service.bus.validation.ServiceBusValidation;
import software.tnb.common.account.AccountFactory;
import software.tnb.common.service.Service;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClientBuilder;
import com.google.auto.service.AutoService;

@AutoService(ServiceBus.class)
public class ServiceBus implements Service {
    private ServiceBusValidation validation;

    private static AzureServiceBusAccount azureServiceBusAccount() {
        return AccountFactory.create(AzureServiceBusAccount.class);
    }

    private static ServiceBusAdministrationClient getAdminClient() {
        return new ServiceBusAdministrationClientBuilder().connectionString(azureServiceBusAccount().connectionString()).buildClient();
    }

    public ServiceBusValidation validation() {
        return validation;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        // no-op
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        validation = new ServiceBusValidation(azureServiceBusAccount(), getAdminClient());
    }
}
