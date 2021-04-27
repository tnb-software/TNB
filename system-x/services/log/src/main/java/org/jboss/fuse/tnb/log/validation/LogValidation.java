package org.jboss.fuse.tnb.log.validation;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

public class LogValidation implements Closeable {
    private Reader r;

    public LogValidation(Reader r) {
        this.r = r;
    }

    public boolean checkMessage(String message) {
        return new BufferedReader(r).lines().anyMatch(s -> s.contains(message));
    }

    public boolean messageRegexCheck(String regex) {
        Pattern p = Pattern.compile(regex);
        return new BufferedReader(r).lines().anyMatch(s -> p.matcher(s).matches());
    }

    public boolean checkMessage(String message, int skipLines) {
        return new BufferedReader(r).lines().skip(skipLines).anyMatch(s -> s.contains(message));
    }

    @Override
    public void close() throws IOException {
        if (r != null) {
            r.close();
        }
    }
}
