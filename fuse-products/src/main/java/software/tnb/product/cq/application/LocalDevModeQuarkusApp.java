package software.tnb.product.cq.application;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;
import software.tnb.product.application.Phase;
import software.tnb.product.cq.configuration.QuarkusConfiguration;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.log.FileLog;
import software.tnb.product.log.stream.LogStream;
import software.tnb.product.util.maven.BuildRequest;
import software.tnb.product.util.maven.Maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Represents a local quarkus app that is run in quarkus dev mode.
 */
public class LocalDevModeQuarkusApp extends LocalQuarkusApp {
    private static final Logger LOG = LoggerFactory.getLogger(LocalDevModeQuarkusApp.class);
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private QuarkusDevRunnable runnable;

    public LocalDevModeQuarkusApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);

        if (QuarkusConfiguration.isQuarkusNative()) {
            throw new RuntimeException("Incompatible configuration - quarkus:dev mode and quarkus native");
        }
    }

    @Override
    protected void buildApp() {
        // no-op in quarkus:dev
    }

    @Override
    public void start() {
        if (shouldRun()) {
            final Path logFile = getLogPath(Phase.RUN);
            BuildRequest.Builder requestBuilder = new BuildRequest.Builder()
                .withBaseDirectory(TestConfiguration.appLocation().resolve(getName()))
                .withArgs("compile", "quarkus:dev")
                // configure the debug port
                .withProperties(Map.of(
                    "debug", TestConfiguration.appDebug() ? (TestConfiguration.appDebugPort() + "") : "false",
                    "suspend", "true"
                ))
                .withLogFile(logFile)
                .withLogMarker(LogStream.marker(getName(), Phase.RUN));

            LOG.info("Running {} application project in quarkus:dev mode", getName());
            runnable = new QuarkusDevRunnable(requestBuilder.build());
            EXECUTOR.submit(runnable);

            WaitUtils.waitFor(new Waiter(() -> logFile.toFile().exists(), "Waiting until the logfile is created"));

            log = new FileLog(logFile);
            // do not start the logstream, as the maven already contains a logstream

            if (TestConfiguration.appDebug()) {
                LOG.warn("App started with debug mode enabled. Connect the debugger to port {}, otherwise the app never reaches ready state",
                    TestConfiguration.appDebugPort());
            }
        }
    }

    @Override
    public void stop() {
        if (runnable != null) {
            LOG.info("Stopping integration {}", getName());
            if (runnable.isRunning()) {
                LOG.debug("Terminating executor service");
                EXECUTOR.shutdownNow();
                try {
                    EXECUTOR.awaitTermination(1, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    LOG.warn("Executor did not terminate normally");
                }
                runnable = null;
            }
        }

        super.stop();
    }

    @Override
    public void kill() {
        stop();
    }

    @Override
    public boolean isReady() {
        return runnable.isRunning();
    }

    @Override
    public boolean isFailed() {
        // either the whole maven process failed before it got to running the quarkus:dev
        // or there is an error log, as quarkus:dev remains running even in case of a failure
        return !runnable.isRunning() || getLog().contains("Quarkus startup failure");
    }

    private static class QuarkusDevRunnable implements Runnable {
        private final BuildRequest request;
        private final CountDownLatch latch = new CountDownLatch(1);

        QuarkusDevRunnable(BuildRequest request) {
            this.request = request;
        }

        @Override
        public void run() {
            Maven.invoke(request);
            // this will count down only if the maven invocation failed before running quarkus:dev, see the comment in isFailed method
            latch.countDown();
        }

        public boolean isRunning() {
            return latch.getCount() != 0;
        }
    }
}
