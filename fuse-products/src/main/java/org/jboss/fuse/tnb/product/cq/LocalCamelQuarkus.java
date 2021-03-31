package org.jboss.fuse.tnb.product.cq;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.utils.MapUtils;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.util.Maven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

@AutoService(Product.class)
public class LocalCamelQuarkus extends Quarkus {
    private static final Logger log = LoggerFactory.getLogger("LocalCamelQuarkus");
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private Path logFile;
    private Process integrationProcess;

    @Override
    public void deploy() {
        Maven.setupMaven();
    }

    @Override
    public void undeploy() {
    }

    @Override
    public void deployIntegration(String name, CodeBlock routeDefinition, String... camelComponents) {
        createApp(name, routeDefinition, camelComponents);
        logFile = TestConfiguration.appLocation().resolve(name + ".log").toAbsolutePath();

        Maven.invoke(
            TestConfiguration.appLocation().resolve(name),
            Arrays.asList("clean", "package"),
            TestConfiguration.isQuarkusNative() ? Collections.singletonList("native") : null,
            MapUtils.toProperties(Map.of(
                "skipTests", "true",
                // todo: tmp due to issue with mongodb in 3.8.0 camel
                "quarkus.platform.version", "1.11.6.Final"
                )
            )
        );

        List<String> cmd = new ArrayList<>();
        String fileName;
        Path integrationTarget = TestConfiguration.appLocation().resolve(name).resolve("target");
        if (TestConfiguration.isQuarkusNative()) {
            fileName = integrationTarget.resolve(name + "-1.0.0-SNAPSHOT-runner").toAbsolutePath().toString();
        } else {
            cmd.addAll(Arrays.asList(System.getProperty("java.home") + "/bin/java", "-jar"));
            // todo: tmp due to issue with mongodb in 3.8.0 camel
            //fileName = integrationTarget.resolve("quarkus-app/quarkus-run.jar").toAbsolutePath().toString();
            fileName = integrationTarget.resolve(name + "-1.0.0-SNAPSHOT-runner.jar").toAbsolutePath().toString();
        }
        cmd.add(fileName);

        if (!new File(fileName).exists()) {
            throw new IllegalArgumentException("Expected file " + fileName + " does not exist, check if the maven build was successful");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        processBuilder.redirectOutput(logFile.toFile());

        try {
            integrationProcess = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException("Unable to start integration process: ", e);
        }

        waitForIntegration(name);
    }

    @Override
    public void waitForIntegration(String name) {
        log.info("Waiting until integration {} is running", name);
        try {
            WaitUtils.waitFor(() -> {
                try {
                    return Files.readString(logFile).contains("started and consuming");
                } catch (IOException e) {
                    // Ignored as the file may not exist yet
                }
                return false;
            }, 60, 1000L);
        } catch (TimeoutException e) {
            throw new RuntimeException("Wait for integration " + name + " failed, check " + logFile.toAbsolutePath().toString());
        }
    }

    @Override
    public void undeployIntegration() {
        if (integrationProcess != null) {
            if (integrationProcess.isAlive()) {
                integrationProcess.destroy();
            }
        }
    }
}
