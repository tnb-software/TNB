package org.jboss.fuse.tnb.common.utils;

import java.util.UUID;

public class StringUtils {
    public static String getRandomAlphanumStringOfLength(int length) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, length);
    }

    public static String replaceUnderscoreWithCamelCase(String s) {
        int underscorePosition = s.indexOf("_");
        while (underscorePosition != -1) {
            s = s.replaceFirst("_.", String.valueOf(Character.toUpperCase(s.charAt(++underscorePosition))));
            underscorePosition = s.indexOf("_", underscorePosition);
        }
        return s;
    }
}
