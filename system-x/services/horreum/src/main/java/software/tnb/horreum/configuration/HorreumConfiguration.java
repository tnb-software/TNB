package software.tnb.horreum.configuration;

import software.tnb.common.config.Configuration;

import java.util.Optional;

public class HorreumConfiguration extends Configuration {

    public static final String URL = "horreum.url";
    public static final String TEST_NAME = "horreum.testname";
    public static final String SCHEMA = "horreum.schema";
    public static final String USER_NAME = "horreum.username";
    public static final String TEST_OWNER = "horreum.testowner";
    public static final String HTTP_LOG_ENABLED = "horreum.http.log.enabled";
    public static final String REQUEST_LOG_ENABLED = "horreum.request.log.enabled";
    public static final String FINGERPRINT_NAME = "horreum.fingerprint.name";
    public static final String TESTRUN_DESCRIPTION = "horreum.testrun.description";
    public static final String HORREUM_UPLOAD_DISABLED = "horreum.upload.disabled";

    public static String getUrl() {
        return getProperty(URL);
    }

    public static String getTestName() {
        return getProperty(TEST_NAME);
    }

    public static String getSchema() {
        return getProperty(SCHEMA);
    }

    public static String getUserName() {
        return getProperty(USER_NAME);
    }

    public static String getTestOwner() {
        return getProperty(TEST_OWNER);
    }

    public static String getTestrunDescription() {
        return getProperty(TESTRUN_DESCRIPTION, "not provided");
    }

    public static boolean isHttpLogEnabled() {
        return getBoolean(HTTP_LOG_ENABLED, false);
    }

    public static boolean isRequestLogEnabled() {
        return getBoolean(REQUEST_LOG_ENABLED, false);
    }

    public static Optional<String> getFingerprintName() {
        return Optional.ofNullable(getProperty(FINGERPRINT_NAME));
    }

    public static boolean isUploadDisabled() {
        return Boolean.parseBoolean(getProperty(HORREUM_UPLOAD_DISABLED, "false"));
    }
}
