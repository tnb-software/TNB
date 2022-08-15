package software.tnb.product.log.stream;

import software.tnb.product.application.Phase;

import org.apache.commons.lang3.StringUtils;

public interface LogStream {
    static String marker(String integrationName) {
        return marker(integrationName, null);
    }

    static String marker(String integrationName, Phase phase) {
        return (phase == null) ? String.format("[%s]", integrationName) : String.format("[%s][%s]", integrationName,
            StringUtils.capitalize(phase.name().toLowerCase()));
    }

    void stop();
}
