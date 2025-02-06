package software.tnb.common.exception;

public class FailureConditionMetException extends RuntimeException {
    public FailureConditionMetException(String message) {
        super(message);
    }

    public FailureConditionMetException(String message, Throwable cause) {
        super(message, cause);
    }
}
