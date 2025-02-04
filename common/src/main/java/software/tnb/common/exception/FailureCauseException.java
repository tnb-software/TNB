package software.tnb.common.exception;

import software.tnb.common.utils.IOUtils;

import java.nio.file.Path;

public class FailureCauseException extends RuntimeException {
    private String description;

    public FailureCauseException(String description, String message) {
        super(message);
        this.description = description;
    }

    public FailureCauseException(Path logFile) {
        this(null, logFile);
    }

    public FailureCauseException(String description, Path logFile) {
        this(description, "Contents of " + logFile.toAbsolutePath() + " below:\n" + IOUtils.readFile(logFile));
    }

    public String getDescription() {
        return description;
    }
}
