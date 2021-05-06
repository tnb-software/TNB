package org.jboss.fuse.tnb.product.application;

import org.jboss.fuse.tnb.common.utils.WaitUtils;

import java.util.regex.Pattern;

public abstract class App {
    private static final Pattern LOG_STARTED_REGEX = Pattern.compile("(?m)^.*Apache Camel.*started in.*$");

    protected final String name;

    public App(String name) {
        this.name = name;
    }

    public abstract void start();

    public abstract void stop();

    public abstract boolean isReady();

    public abstract boolean isFailed();

    public abstract String getLogs();

    public void waitUntilReady() {
        WaitUtils.waitFor(this::isReady, this::isFailed, 1000L, "Waiting until the integration " + name + " is running");
    }

    protected boolean isCamelStarted() {
        return getLogs().lines().anyMatch(s -> LOG_STARTED_REGEX.matcher(s).find());
    }
}
