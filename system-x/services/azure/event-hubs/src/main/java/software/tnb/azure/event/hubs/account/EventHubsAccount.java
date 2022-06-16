package software.tnb.azure.event.hubs.account;

import software.tnb.azure.common.account.AzureAccount;

public class EventHubsAccount extends AzureAccount {
    private String event_hubs_namespace;
    private String event_hub_name;
    private String event_hub_shared_access_name;
    private String event_hub_shared_access_key;

    public void setEvent_hubs_namespace(String event_hubs_namespace) {
        this.event_hubs_namespace = event_hubs_namespace;
    }

    public void setEvent_hub_name(String event_hub_name) {
        this.event_hub_name = event_hub_name;
    }

    public void setEvent_hub_shared_access_name(String event_hub_shared_access_name) {
        this.event_hub_shared_access_name = event_hub_shared_access_name;
    }

    public void setEvent_hub_shared_access_key(String event_hub_shared_access_key) {
        this.event_hub_shared_access_key = event_hub_shared_access_key;
    }

    public String getEventHubsNamespace() {
        return event_hubs_namespace;
    }

    public String getEvenHubName() {
        return event_hub_name;
    }

    public String getEventHubSharedAccessName() {
        return event_hub_shared_access_name;
    }

    public String getEventHubSharedAccessKey() {
        return event_hub_shared_access_key;
    }
}
