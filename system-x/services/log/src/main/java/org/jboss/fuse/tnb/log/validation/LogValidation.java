package org.jboss.fuse.tnb.log.validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class LogValidation {
    private Reader r;

    public LogValidation(Reader r) {
        this.r = r;
    }

    public boolean checkMessage(String message) {
        try (BufferedReader br = new BufferedReader(r)) {
            return br.lines().anyMatch(s -> s.contains(message));
        } catch (IOException e) {
            throw new RuntimeException("Can't read application log.");
        }
    }
}
