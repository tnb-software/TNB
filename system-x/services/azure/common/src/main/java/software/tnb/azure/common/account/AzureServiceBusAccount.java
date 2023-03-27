package software.tnb.azure.common.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

public class AzureServiceBusAccount implements Account, WithId {
    private String connection_string;

    @Override
    public String credentialsId() {
        return "azure-servicebus";
    }

    public void setConnection_string(String connection_string) {
        this.connection_string = connection_string;
    }

    public String connectionString() {
        return connection_string;
    }
}
