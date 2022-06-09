package software.tnb.product.log;

import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.StringUtils;
import software.tnb.product.rp.Attachments;

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

    @Override
    public void save() {
        Attachments.addAttachment(logFile);
    }
}
