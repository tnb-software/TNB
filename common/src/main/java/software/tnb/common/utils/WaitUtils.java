package software.tnb.common.utils;

import static org.awaitility.Awaitility.await;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.exception.FailureConditionMetException;
import software.tnb.common.exception.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public final class WaitUtils {
    private static final Logger LOG = LoggerFactory.getLogger(WaitUtils.class);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

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
     */
    public static void waitFor(BooleanSupplier check, String logMessage) throws TimeoutException {
        waitFor(check, 24, 5000L, logMessage);
    }

    /**
     * Waits until the check return true.
     *
     * @param resourceCheck success condition
     * @param retries number of retries
     * @param waitTime wait time between the retries
     * @param logMessage log message that will be printed out before waiting
     * @throws TimeoutException when the check isn't true after the time expires
     */
    public static void waitFor(BooleanSupplier resourceCheck, int retries, long waitTime, String logMessage) throws TimeoutException {
        LOG.info(logMessage);
        await()
            .atMost(retries * waitTime, TimeUnit.MILLISECONDS)
            .pollInterval(waitTime, TimeUnit.MILLISECONDS)
            .pollDelay(0, TimeUnit.SECONDS)
            .conditionEvaluationListener(condition -> LOG.debug("Condition not met yet, sleeping for {}", waitTime))
            .until(resourceCheck::getAsBoolean);
        LOG.debug("Done waiting");
    }

    /**
     * Waits until the check or fail condition return true.
     * <p>
     * If a wait duration specified by {@link TestConfiguration#testWaitKillTimeout()} is reached, the wait is killed to prevent infinite waiting
     *
     * @param check booleansupplier instance
     * @param fail booleansupplier instance
     * @param timeout wait time between the retries
     * @param logMessage log message that will be printed out before waiting
     * @throws FailureConditionMetException when the fail condition is true
     */
    public static void waitFor(BooleanSupplier check, BooleanSupplier fail, long timeout, String logMessage) throws FailureConditionMetException {
        await()
            .atMost(TestConfiguration.testWaitKillTimeout())
            .pollInterval(timeout, TimeUnit.MILLISECONDS)
            .pollDelay(0, TimeUnit.MILLISECONDS)
            .failFast(fail::getAsBoolean)
            .conditionEvaluationListener(condition -> LOG.debug("Condition not met yet, sleeping for {}", timeout))
            .until(check::getAsBoolean);
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
        final Future<T> future = EXECUTOR_SERVICE.submit(callable);
        try {
            return future.get(waitTime.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Unable to get callable result: ", e);
        } catch (java.util.concurrent.TimeoutException e) {
            throw new TimeoutException(e);
        }
    }
}
