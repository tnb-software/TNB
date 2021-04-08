package org.jboss.fuse.tnb.common.utils;

import java.util.Random;

public class StringUtils {
    public static String generateTemporaryNamespaceName(){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return "tnb-test-" + generatedString;
    }
}
