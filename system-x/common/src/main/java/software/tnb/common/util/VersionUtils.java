package software.tnb.common.util;

import software.tnb.common.deployment.Deployable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VersionUtils {

    private VersionUtils() {
    }

    public static String extractFromLogs(Object service, Pattern pattern) {
        if (service instanceof Deployable d) {
            try {
                String logs = d.getLogs();
                if (logs != null) {
                    Matcher m = pattern.matcher(logs);
                    if (m.find()) {
                        return m.group(1);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
