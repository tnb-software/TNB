package software.tnb.hyperfoil.service;

import software.tnb.common.config.Configuration;

import java.util.Optional;

public class HyperfoilConfiguration extends Configuration {

    public static final String KEEP_RUNNING = "hyperfoil.keep.running";

    public static final String AGENT_LOG_CONF = "hyperfoil.agent.log.conf";

    public static final String AGENT_LOG_MAP_CONFIG = "hyperfoil.agent.log.mapconfig";
    public static final String AGENT_LOG_FILE_NAME = "hyperfoil.agent.log.filename";

    public static final String HTTP_LOG_ENABLED = "hyperfoil.http.log.enabled";

    public static final String RETRY_BACKOFF_DELAY_IN_SEC = "hyperfoil.http.retry.policy.backoff.delayinsec";
    public static final String RETRY_BACKOFF_MAX_DELAY_IN_SEC = "hyperfoil.http.retry.policy.backoff.maxdelayinsec";
    public static final String RETRY_NUMBER_OF_RETRIES = "hyperfoil.http.retry.policy.retries";
    public static final String RETRY_POLICY_ENABLED = "hyperfoil.http.retry.policy.enabled";

    private static final String HYPERFOIL_VERSION = "hyperfoil.version";

    public static boolean keepRunning() {
        return getBoolean(KEEP_RUNNING, false);
    }

    public static Optional<String> agentLogConf() {
        return Optional.ofNullable(getProperty(AGENT_LOG_CONF));
    }

    public static String agentLogMapConfig() {
        return getProperty(AGENT_LOG_MAP_CONFIG, "hf-agent-log");
    }

    public static String agentLogFileName() {
        return getProperty(AGENT_LOG_FILE_NAME, "log4j2-agent.xml");
    }

    public static boolean isHttpLogEnabled() {
        return getBoolean(HTTP_LOG_ENABLED, false);
    }

    public static int retryBackoffDelayInSec() {
        return getInteger(RETRY_BACKOFF_DELAY_IN_SEC, 1);
    }

    public static int retryBackoffMaxDelayInSec() {
        return getInteger(RETRY_BACKOFF_MAX_DELAY_IN_SEC, 30);
    }

    public static int retryNumberOfRetries() {
        return getInteger(RETRY_NUMBER_OF_RETRIES, 10);
    }

    public static boolean isRetryPolicyEnabled() {
        return getBoolean(RETRY_POLICY_ENABLED, false);
    }

    public static String getHyperfoilVersion() {
        return getProperty(HYPERFOIL_VERSION, "latest");
    }
}
