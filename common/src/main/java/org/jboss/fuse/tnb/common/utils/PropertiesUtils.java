package org.jboss.fuse.tnb.common.utils;

import java.util.Properties;

public class PropertiesUtils {
    public static Properties toCamelCaseProperties(Properties underscoreProperties) {
        Properties camelCaseProperties = new Properties();
        underscoreProperties.entrySet().stream()
            .forEach(entry -> camelCaseProperties.put(StringUtils.replaceUnderscoreWithCamelCase(entry.getKey().toString()), entry.getValue()));
        return camelCaseProperties;
    }
}
