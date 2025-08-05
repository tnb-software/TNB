package software.tnb.common.utils;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.exception.FailureConditionMetException;
import software.tnb.common.exception.TimeoutException;
import software.tnb.common.utils.waiter.Waiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BooleanSupplier;

public final class WaitUtils {
    private static final Logger LOG = LoggerFactory.getLogger(WaitUtils.class);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(1);

    private WaitUtils() {
    }

    /**
     * Sleeps for a given time.
     *
     * @param timeout timeout
     */
    public static void sleep(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Waits until the check return true.
     *
     * @param check booleansupplier instance
     * @param logMessage log message that will be printed out before waiting
     * @throws TimeoutException when the check isn't true after the time expires
     *
     * @deprecated Use {@link #waitFor(Waiter)}
     */
    @Deprecated
    public static void waitFor(BooleanSupplier check, String logMessage) throws TimeoutException {
        waitFor(new Waiter(check, logMessage));
    }

    /**
     * Waits until the check return true.
     *
     * @param resourceCheck success condition
     * @param retries number of retries
     * @param waitTime wait time between the retries
     * @param logMessage log message that will be printed out before waiting
     * @throws TimeoutException when the check isn't true after the time expires
     *
     * @deprecated Use {@link #waitFor(Waiter)}
     */
    @Deprecated
    public static void waitFor(BooleanSupplier resourceCheck, int retries, long waitTime, String logMessage) throws TimeoutException {
        waitFor(new Waiter(resourceCheck, logMessage).timeout(retries, waitTime));
    }

    /**
     * Waits until either success check or failure check returns true.
     *
     * @param check success condition
     * @param fail failure condition
     * @param timeout wait time between the retries
     * @param logMessage log message that will be printed out before waiting
     * @throws FailureConditionMetException when failure condition is true
     *
     * @deprecated Use {@link #waitFor(Waiter)}
     */
    @Deprecated
    public static void waitFor(BooleanSupplier check, BooleanSupplier fail, long timeout, String logMessage) throws FailureConditionMetException {
        waitFor(new Waiter(check, logMessage).failureCondition(fail).retryTimeout(timeout));
    }

    /**
     * Waits until the wait defined by the waiter either succeedes, fails, timeouts or is killed by the global wait timeout.
     * @param waiter wait definition
     */
    public static void waitFor(Waiter waiter) {
        LOG.info(waiter.getLogMessage());

        int retries = waiter.getRetries();
        Instant start = Instant.now();

        while (!waiter.getCondition().getAsBoolean()) {
            // if there is a failure condition defined in the waiter, ignore the retries count, as the wait ends either
            // when success/failure is reached or it is killed by the wait kill timeout
            if (waiter.getFailureCondition() != null) {
                if (waiter.getFailureCondition().getAsBoolean()) {
                    throw waiter.getFailureException();
                } else if (Duration.between(start, Instant.now()).compareTo(TestConfiguration.testWaitKillTimeout()) > 0) {
                    LOG.error("Wait killed after {} minutes", TestConfiguration.testWaitKillTimeout().toMinutes());
                    break;
                }
            } else {
                retries--;
                if (retries < 0) {
                    throw waiter.getTimeoutException();
                }
            }

            LOG.debug("Condition not met yet, sleeping for {}", waiter.getRetryTimeout());
            sleep(waiter.getRetryTimeout());
        }
        LOG.debug("Done waiting");
    }

    /**
     * Runs the given callable and aborts its execution if it takes too long.
     *
     * @param callable callable to run
     * @param <T> return type
     * @return callable result or TimeoutException
     */
    public static <T> T withTimeout(Callable<T> callable) {
        return withTimeout(callable, TestConfiguration.testWaitTime());
    }

    /**
     * Runs the given callable and aborts its execution if it takes too long.
     *
     * @param callable callable to run
     * @param waitTime wait time
     * @param <T> return type
     * @return callable result or TimeoutException
     */
    public static <T> T withTimeout(Callable<T> callable, Duration waitTime) {
        Instant end = Instant.now().plus(waitTime);
        final Future<T> future = EXECUTOR_SERVICE.submit(callable);
        while (Instant.now().isBefore(end) && !future.isDone()) {
            sleep(100L);
        }
        if (!future.isDone()) {
            future.cancel(true);
            throw new TimeoutException("Timeout exceeded");
        } else {
            try {
                return future.get();
            } catch (Exception e) {
                throw new RuntimeException("Unable to get callable result: ", e);
            }
        }
    }
}
