package software.tnb.azure.event.hubs.account;

import software.tnb.common.account.Account;
import software.tnb.common.account.WithId;

import org.apache.commons.lang3.StringUtils;

public class AzureEventHubsAccount implements Account, WithId {
    private String connection_string;

    @Override
    public String credentialsId() {
        return "tnb-azure-eventhubs";
    }

    // Endpoint=sb://<namespace>.servicebus.windows.net/;SharedAccessKeyName=<keyname>;SharedAccessKey=<accesskey>;EntityPath=<eventhub>
    public String connectionString() {
        return connection_string;
    }

    public void setConnection_string(String connection_string) {
        this.connection_string = connection_string;
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
