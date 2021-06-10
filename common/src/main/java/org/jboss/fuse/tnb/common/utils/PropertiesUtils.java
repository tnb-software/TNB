package org.jboss.fuse.tnb.common.utils;

import java.util.Properties;

public final class PropertiesUtils {
    private PropertiesUtils() {
    }

    public static Properties toCamelCaseProperties(Properties underscoreProperties) {
        Properties camelCaseProperties = new Properties();
        underscoreProperties.forEach((key, value) -> camelCaseProperties.put(StringUtils.replaceUnderscoreWithCamelCase(key.toString()), value));
        return camelCaseProperties;
    }
}
