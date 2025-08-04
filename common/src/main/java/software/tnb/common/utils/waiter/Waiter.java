package software.tnb.common.utils.waiter;

import software.tnb.common.exception.FailureConditionMetException;
import software.tnb.common.exception.TimeoutException;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Class to define the waiting conditions for the {@link software.tnb.common.utils.WaitUtils#waitFor(Waiter)} method.
 */
public class Waiter {
    private final BooleanSupplier condition;
    private final String logMessage;

    private BooleanSupplier failureCondition;
    private int retries = 24;
    private long retryTimeout = 5000L;
    private String failureMessage = "Specified fail condition met";

    private Supplier<FailureConditionMetException> failureException = () -> new FailureConditionMetException(failureMessage);
    private Supplier<TimeoutException> timeoutException = () -> new TimeoutException("Timeout exceeded");

    /**
     * Constructor.
     * @param condition success condition
     * @param logMessage log message printed at the start of the wait
     */
    public Waiter(BooleanSupplier condition, String logMessage) {
        this.condition = condition;
        this.logMessage = logMessage;
    }

    /**
     * Retry time setter - how long to wait for each retry.
     * @param retryTime long value
     * @return this
     */
    public Waiter retryTimeout(long retryTime) {
        this.retryTimeout = retryTime;
        return this;
    }

    /**
     * Timeout setter in the form of retries count and retry timeout.
     * @param retries int value
     * @param retryTimeout long value
     * @return this
     */
    public Waiter timeout(int retries, long retryTimeout) {
        this.retries = retries;
        this.retryTimeout = retryTimeout;
        return this;
    }

    /**
     * Failure condition setter - this causes the wait to ignore the retries and end either when success or failure condition is true.
     * @param condition failure condition
     * @return this
     */
    public Waiter failureCondition(BooleanSupplier condition) {
        this.failureCondition = condition;
        return this;
    }

    /**
     * Failure message setter - a message that is printed when the failure condition is true.
     * @param message failure message
     * @return this
     */
    public Waiter failureMessage(String message) {
        this.failureMessage = message;
        return this;
    }

    /**
     * Supplier for the FailureConditionMetException - this supplier is used when the failure condition is true.
     * @param supplier exception supplier
     * @return this
     */
    public Waiter failureException(Supplier<FailureConditionMetException> supplier) {
        this.failureException = supplier;
        return this;
    }

    /**
     * Supplier for the TimeoutException - this supplier is used when the wait times out.
     * @param exception exception supplier
     * @return this
     */
    public Waiter timeoutException(Supplier<TimeoutException> exception) {
        this.timeoutException = exception;
        return this;
    }

    public BooleanSupplier getCondition() {
        return condition;
    }

    public BooleanSupplier getFailureCondition() {
        return failureCondition;
    }

    public int getRetries() {
        return retries;
    }

    public long getRetryTimeout() {
        return retryTimeout;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public FailureConditionMetException getFailureException() {
        return failureException.get();
    }

    public TimeoutException getTimeoutException() {
        return timeoutException.get();
    }
}
