package org.jboss.fuse.tnb.common.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public final class StringUtils {
    private StringUtils() {
    }

    public static String getRandomAlphanumStringOfLength(int length) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, length);
    }

    public static String replaceCamelCaseWith(String s, String replacement) {
        String ret = Character.toLowerCase(s.charAt(0)) + s.substring(1);
        if (org.apache.commons.lang3.StringUtils.isAllLowerCase(ret)) {
            return ret;
        } else {
            return ret.replaceAll("([A-Z])", replacement + "$1").toLowerCase();
        }
    }

    public static String replaceUnderscoreWithCamelCase(String s) {
        int underscorePosition = s.indexOf("_");
        while (underscorePosition != -1) {
            s = s.replaceFirst("_.", String.valueOf(Character.toUpperCase(s.charAt(++underscorePosition))));
            underscorePosition = s.indexOf("_", underscorePosition);
        }
        return s;
    }

    public static String removeColorCodes(String s) {
        return s.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    public static String base64Encode(String toEncode) {
        return new String(Base64.getEncoder().encode(toEncode.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }
}
