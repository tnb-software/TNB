package org.jboss.fuse.tnb.log.validation;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

public class LogValidation implements Closeable {
    private Reader r;

    public LogValidation(Reader r) {
        this.r = r;
    }

    public boolean checkMessage(String message) {
        return new BufferedReader(r).lines().anyMatch(s -> s.contains(message));
    }

    @Override
    public void close() throws IOException {
        r.close();
    }
}
