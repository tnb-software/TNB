package software.tnb.azure.service.bus.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class AzureServiceBusAccount implements Account, WithId {
    private String connectionString;

    @Override
    public String credentialsId() {
        return "azure_servicebus";
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String connectionString() {
        return connectionString;
    }
}
