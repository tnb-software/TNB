package software.tnb.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class VersionUtils {

    private static final Logger LOG = LoggerFactory.getLogger(VersionUtils.class);
    private static final String RELEASES_LATEST_URL = "https://api.github.com/repos/%s/releases/latest";

    private static VersionUtils instance;

    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    private VersionUtils() {
    }

    public static VersionUtils getInstance() {
        instance = Optional.ofNullable(instance).orElseGet(VersionUtils::new);
        return instance;
    }

    /**
     * Returns the latest GitHub release tag for a specific repository
     * Example: "eclipse/jkube" => v1.9.1
     * @param githubRepo The repo in GitHub (example: eclipse/jkube)
     * @param fallbackValue The value returned in case of error
     * @return String, the tag name of the latest release
     */
    public String getLatestGitHubReleaseTag(final String githubRepo, final String fallbackValue) {
        return cache.computeIfAbsent(githubRepo, k -> {
            final HTTPUtils.Response response = HTTPUtils.getInstance()
                .get(String.format(RELEASES_LATEST_URL, k), false);
            String version = null;
            if (response.isSuccessful()) {
                try {
                    version = new ObjectMapper().readValue(response.getBody(), JsonNode.class).get("tag_name").textValue();
                } catch (final Exception e) {
                    //ignore errors
                    LOG.warn("error on getting response from github repo {}: {}", k, e.getMessage());
                }
            }
            return version != null ? version : fallbackValue;
        });
    }
}
