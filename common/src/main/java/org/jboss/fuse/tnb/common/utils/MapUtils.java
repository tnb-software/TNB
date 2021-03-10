package org.jboss.fuse.tnb.common.utils;

import java.util.HashMap;
import java.util.Map;

public final class MapUtils {
    private MapUtils() {
    }

    public static Map<String, String> map(String... values) {
        final Map<String, String> result = new HashMap<>();
        for (int i = 0; i + 1 < values.length; i += 2) {
            result.put(values[i], values[i + 1]);
        }
        return result;
    }
}
