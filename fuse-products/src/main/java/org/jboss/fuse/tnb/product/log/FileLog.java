package org.jboss.fuse.tnb.product.log;

import org.jboss.fuse.tnb.common.utils.IOUtils;

import java.nio.file.Path;

public class FileLog extends Log {
    private final Path logFile;

    public FileLog(Path file) {
        this.logFile = file;
    }

    @Override
    public String toString() {
        return IOUtils.readFile(logFile);
    }
}
