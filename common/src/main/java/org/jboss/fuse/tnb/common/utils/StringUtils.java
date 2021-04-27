package org.jboss.fuse.tnb.common.utils;

import java.util.UUID;

public class StringUtils {
    public static String getRandomAlphanumStringOfLength(int length) {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, length);
    }
}
