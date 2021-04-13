package org.jboss.fuse.tnb.product.cq;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.utils.MapUtils;
import org.jboss.fuse.tnb.common.utils.WaitUtils;
import org.jboss.fuse.tnb.product.LocalProduct;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.util.Maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@AutoService(Product.class)
public class LocalCamelQuarkus extends LocalProduct implements Quarkus {
    private static final Logger LOG = LoggerFactory.getLogger(LocalCamelQuarkus.class);
    private Path logFile;
    private Process integrationProcess;

    public void createIntegration(String name, IntegrationBuilder integrationBuilder, String... camelComponents) {
        createApp(name, integrationBuilder, camelComponents);
        logFile = TestConfiguration.appLocation().resolve(name + ".log").toAbsolutePath();
        LOG.debug("Integration log file: " + logFile);

        LOG.info("Building {} application project ({})", name, TestConfiguration.isQuarkusNative() ? "native" : "JVM");
        Maven.invoke(
            TestConfiguration.appLocation().resolve(name),
            Arrays.asList("clean", "package"),
            TestConfiguration.isQuarkusNative() ? Collections.singletonList("native") : null,
            MapUtils.toProperties(Map.of(
                "skipTests", "true"
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
            fileName = integrationTarget.resolve("quarkus-app/quarkus-run.jar").toAbsolutePath().toString();
        }
        cmd.add(fileName);

        if (!new File(fileName).exists()) {
            throw new IllegalArgumentException("Expected file " + fileName + " does not exist, check if the maven build was successful");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        processBuilder.redirectOutput(logFile.toFile());

        LOG.info("Starting integration {}", name);
        LOG.debug("ProcessBuilder command: " + String.join(" ", cmd));
        try {
            integrationProcess = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException("Unable to start integration process: ", e);
        }

        waitForIntegration(name);
    }

    @Override
    public void waitForIntegration(String name) {
        LOG.info("Waiting until integration {} is running", name);
        WaitUtils.waitFor(() -> {
            try {
                String content = Files.readString(logFile);
                // new style in 3.9 camel
                return content.contains("started in") || content.contains("started and consuming");
            } catch (IOException e) {
                // Ignored as the file may not exist yet
            }
            return false;
        }, 60, 1000L, String.format("Waiting until integration %s is running", name));
    }

    @Override
    public void removeIntegration() {
        if (integrationProcess != null) {
            if (integrationProcess.isAlive()) {
                LOG.debug("Killing integration process");
                integrationProcess.destroy();
            }
        }
    }
}
