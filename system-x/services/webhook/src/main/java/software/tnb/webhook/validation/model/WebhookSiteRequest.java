package software.tnb.webhook.validation.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class WebhookSiteRequest {

    private String uuid;

    private Map<String, List<String>> headers;

    private String content;

    private LocalDateTime created_at;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.created_at = createdAt;
    }
}
