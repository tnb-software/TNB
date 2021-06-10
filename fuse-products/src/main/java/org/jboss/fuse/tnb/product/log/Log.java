package org.jboss.fuse.tnb.product.log;

import java.util.regex.Pattern;

public abstract class Log {
    public abstract String toString();

    public boolean contains(String message) {
        return toString().contains(message);
    }

    public boolean containsRegex(String regex) {
        return containsRegex(Pattern.compile(regex));
    }

    public boolean containsRegex(Pattern pattern) {
        return toString().lines().anyMatch(s -> pattern.matcher(s).matches());
    }

    public boolean containsAfter(String message, int skipLines) {
        return toString().lines().skip(skipLines).anyMatch(s -> s.contains(message));
    }
}
