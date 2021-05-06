package org.jboss.fuse.tnb.log.validation;

import java.util.regex.Pattern;

public class LogValidation {
    public boolean checkMessage(String log, String message) {
        return log.contains(message);
    }

    public boolean messageRegexCheck(String log, String regex) {
        Pattern p = Pattern.compile(regex);
        return log.lines().anyMatch(s -> p.matcher(s).matches());
    }

    public boolean checkMessage(String log, String message, int skipLines) {
        return log.lines().skip(skipLines).anyMatch(s -> s.contains(message));
    }
}
