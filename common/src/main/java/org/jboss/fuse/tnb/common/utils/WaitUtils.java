package org.jboss.fuse.tnb.common.utils;

import org.jboss.fuse.tnb.common.exception.FailureConditionMetException;

import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

public final class WaitUtils {
    private WaitUtils() {
    }

    public static void sleep(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException ignored) {
        }
    }

    public static void waitFor(BooleanSupplier resourceCheck, int retries, long waitTime) throws TimeoutException {
        boolean state;
        do {
            state = resourceCheck.getAsBoolean();

            if (!state) {
                retries--;
                sleep(waitTime);
            }
        } while (!state && retries > 0);

        if (!state) {
            throw new TimeoutException("Timeout exceeded");
        }
    }

    public static void waitFor(BooleanSupplier check, BooleanSupplier fail, long timeout) throws FailureConditionMetException {
        while (true) {
            if (check.getAsBoolean()) {
                break;
            } else if (fail.getAsBoolean()) {
                throw new FailureConditionMetException("Specified fail condition met");
            } else {
                sleep(timeout);
            }
        }
    }
}
