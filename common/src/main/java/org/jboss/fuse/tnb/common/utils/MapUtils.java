package org.jboss.fuse.tnb.common.utils;

import java.util.Map;
import java.util.Properties;

public final class MapUtils {
    public static Properties toProperties(Map<String, String> map) {
        Properties properties = new Properties();
        properties.putAll(map);
        return properties;
    }
}
