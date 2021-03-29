package org.jboss.fuse.tnb.common.utils;

import org.jboss.fuse.tnb.common.exception.FailureConditionMetException;
import org.jboss.fuse.tnb.common.exception.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BooleanSupplier;

public final class WaitUtils {
    private static final Logger LOG = LoggerFactory.getLogger(WaitUtils.class);

    private WaitUtils() {
    }

    /**
     * Sleeps for a given time.
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
     * @param check booleansupplier instance
     * @param logMessage log message that will be printed out before waiting
     * @throws TimeoutException when the check isn't true after the time expires
     */
    public static void waitFor(BooleanSupplier check, String logMessage) throws TimeoutException {
        waitFor(check, 24, 5000L, logMessage);
    }

    /**
     * Waits until the check return true.
     * @param resourceCheck booleansupplier instance
     * @param retries number of retires
     * @param waitTime wait time between the retries
     * @param logMessage log message that will be printed out before waiting
     * @throws TimeoutException when the check isn't true after the time expires
     */
    public static void waitFor(BooleanSupplier resourceCheck, int retries, long waitTime, String logMessage) throws TimeoutException {
        LOG.info(logMessage);
        boolean state;
        do {
            state = resourceCheck.getAsBoolean();

            if (!state) {
                LOG.debug("Condition not met yet, sleeping for {}", waitTime);
                retries--;
                sleep(waitTime);
            }
        } while (!state && retries > 0);

        if (!state) {
            throw new TimeoutException("Timeout exceeded");
        }
        LOG.debug("Done waiting");
    }

    /**
     * Waits until the check or fail condition return true.
     * @param check booleansupplier instance
     * @param fail booleansupplier instance
     * @param timeout wait time between the retries
     * @param logMessage log message that will be printed out before waiting
     * @throws FailureConditionMetException when the fail condition is true
     */
    public static void waitFor(BooleanSupplier check, BooleanSupplier fail, long timeout, String logMessage) throws FailureConditionMetException {
        LOG.info(logMessage);
        while (true) {
            if (check.getAsBoolean()) {
                break;
            } else if (fail.getAsBoolean()) {
                throw new FailureConditionMetException("Specified fail condition met");
            } else {
                LOG.debug("Condition not met yet, sleeping for {}", timeout);
                sleep(timeout);
            }
        }
        LOG.debug("Done waiting");
    }
}
