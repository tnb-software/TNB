package software.tnb.azure.service.bus.service;

import software.tnb.azure.service.bus.account.AzureServiceBusAccount;
import software.tnb.azure.service.bus.validation.ServiceBusValidation;
import software.tnb.common.service.Service;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClientBuilder;
import com.google.auto.service.AutoService;

@AutoService(ServiceBus.class)
public class ServiceBus extends Service<AzureServiceBusAccount, ServiceBusAdministrationClient, ServiceBusValidation> {
    @Override
    protected ServiceBusAdministrationClient client() {
        if (client == null) {
            client = new ServiceBusAdministrationClientBuilder().connectionString(account().connectionString()).buildClient();
        }
        return client;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        // no-op
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        validation = new ServiceBusValidation(account(), client());
    }
}
