package software.tnb.azure.event.hubs.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

import org.apache.commons.lang3.StringUtils;

public class AzureEventHubsAccount implements Account, WithId {
    private String connectionString;

    @Override
    public String credentialsId() {
        return "azure_eventhubs";
    }

    // Endpoint=sb://<namespace>.servicebus.windows.net/;SharedAccessKeyName=<keyname>;SharedAccessKey=<accesskey>;EntityPath=<eventhub>
    public String connectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String namespace() {
        return StringUtils.substringBetween(connectionString().split(";")[0], "sb://", ".servicebus");
    }

    public String accessKeyName() {
        return connectionString().split(";")[1].split("=")[1];
    }

    public String accessKey() {
        return connectionString().split(";")[2].split("=")[1];
    }

    public String eventHubName() {
        return connectionString().split(";")[3].split("=")[1];
    }
}
