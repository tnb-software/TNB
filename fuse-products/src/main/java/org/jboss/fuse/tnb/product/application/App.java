package org.jboss.fuse.tnb.product.application;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.log.Log;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public abstract class App {
    private static final Pattern LOG_STARTED_REGEX = Pattern.compile("(?m)^.*Apache Camel.*started in.*$");
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    protected final String name;
    protected Log log;

    public App(String name) {
        this.name = name;
        ensureDirNotPresent();
    }

    private void ensureDirNotPresent() {
        final File target = TestConfiguration.appLocation().resolve(name).toFile();
        if (target.exists()) {
            try {
                LOG.debug("Deleting directory with existing application sources {}", target);
                FileUtils.deleteDirectory(target);
            } catch (IOException e) {
                throw new RuntimeException("Couldn't delete existing application sources directory", e);
            }
        }
    }

    public abstract void start();

    public abstract void stop();

    public abstract boolean isReady();

    public abstract boolean isFailed();

    public Log getLog() {
        return log;
    }

    public String getName() {
        return name;
    }

    public void waitUntilReady() {
        WaitUtils.waitFor(this::isReady, this::isFailed, 1000L, "Waiting until the integration " + name + " is running");
    }

    protected boolean isCamelStarted() {
        return getLog().containsRegex(LOG_STARTED_REGEX);
    }
}
