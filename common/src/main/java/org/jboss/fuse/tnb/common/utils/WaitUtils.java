package org.jboss.fuse.tnb.common.utils;

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
//                LOG.debug("The resource is not yet available. Waiting {} seconds before retrying",
//                    TimeUnit.MILLISECONDS.toSeconds(waitTime));
                retries--;
                sleep(waitTime);
            }
        } while (!state && retries > 0);

        if (!state) {
            throw new TimeoutException("todo");
        }
    }
}
