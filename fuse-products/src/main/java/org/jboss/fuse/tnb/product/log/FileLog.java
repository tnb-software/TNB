package org.jboss.fuse.tnb.product.log;

import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.common.utils.StringUtils;

import java.nio.file.Path;

public class FileLog extends Log {
    private final Path logFile;

    public FileLog(Path file) {
        this.logFile = file;
    }

    @Override
    public String toString() {
        return StringUtils.removeColorCodes(IOUtils.readFile(logFile));
    }
}
