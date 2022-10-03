package software.tnb.webhook.validation.model;

import java.util.List;

public class WebhookSiteRequests {
    private List<WebhookSiteRequest> data;

    public List<WebhookSiteRequest> getData() {
        return data;
    }

    public void setData(List<WebhookSiteRequest> data) {
        this.data = data;
    }
}
