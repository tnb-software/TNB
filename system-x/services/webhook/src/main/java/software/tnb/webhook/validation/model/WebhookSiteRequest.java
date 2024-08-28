package software.tnb.webhook.validation.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record WebhookSiteRequest(String method, String uuid, Map<String, List<String>> headers, String content, LocalDateTime created_at) {
}
