package org.jboss.fuse.tnb.product.util.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Executor {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private Executor() {
    }

    public static ExecutorService get() {
        return EXECUTOR_SERVICE;
    }
}
