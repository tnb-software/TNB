package org.jboss.fuse.tnb.product.deploystrategy.impl;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.product.deploystrategy.OpenshiftBaseDeployer;
import org.jboss.fuse.tnb.product.deploystrategy.OpenshiftDeployStrategy;
import org.jboss.fuse.tnb.product.deploystrategy.OpenshiftDeployStrategyType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AutoService(OpenshiftDeployStrategy.class)
public class DevfileStrategy extends OpenshiftBaseDeployer {

    private static final Logger LOG = LoggerFactory.getLogger(DevfileStrategy.class);

    private static final String SB_DEVFILE = "https://raw.githubusercontent.com/mcarlett/registry/ubi/stacks/java-springboot-ubi8/devfile.yaml";

    @Override
    public ProductType[] products() {
        return new ProductType[]{ProductType.CAMEL_SPRINGBOOT};
    }

    @Override
    public OpenshiftDeployStrategyType deployType() {
        return OpenshiftDeployStrategyType.DEVFILE;
    }

    @Override
    public void deploy(String name) {
        try {
            login(name);
            runOdoCmd(Arrays.asList("create", "java-springboot-ubi8", "--app", name, "--context", "."
                , "--devfile", SB_DEVFILE)
                , TestConfiguration.appLocation().resolve(name + "-devfile.log").toFile(), name);

            setEnvVar(name, "MAVEN_MIRROR_URL", TestConfiguration.mavenRepository());

            runOdoCmd(Arrays.asList("push", "--show-log", "-v", "5")
                , TestConfiguration.appLocation().resolve(name + "-deploy.log").toFile(), name);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void undeploy(String name) {
        try {
            login(name);
            final File logFile = TestConfiguration.appLocation().resolve(name + "-undeploy.log").toFile();
            runOdoCmd(Arrays.asList("delete", "--app", name, "-f"), logFile, name);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void setEnvVar(String name, String envName, String envValue) throws IOException, InterruptedException {
        runOdoCmd(Arrays.asList("config", "set", "--env", String.format("%s=%s", envName, envValue)), name);
    }

    private void login(String name) throws IOException, InterruptedException {
        runOdoCmd(Arrays.asList("login", OpenshiftConfiguration.openshiftUrl(),
            String.format("--username=%s", OpenshiftConfiguration.openshiftUsername()),
            String.format("--password=%s", OpenshiftConfiguration.openshiftPassword())
        ), name);
        runOdoCmd(Arrays.asList("project", "set", OpenshiftConfiguration.openshiftNamespace()), name);
    }

    private void runOdoCmd(final List<String> args, String name) throws IOException, InterruptedException {
        this.runOdoCmd(args, null, name);
    }

    private void runOdoCmd(final List<String> args, File logFile, String name) throws IOException, InterruptedException {
        final String odoBinaryPath = System.getProperty("odo.path", IOUtils.getExecInPath("odo"));
        assert odoBinaryPath != null;
        final Path baseDir = TestConfiguration.appLocation().resolve(name);

        final List<String> cmd = new ArrayList<>(args.size() + 1);
        cmd.add(odoBinaryPath);
        cmd.addAll(args);
        cmd.add("--kubeconfig");
        cmd.add(OpenshiftConfiguration.openshiftKubeconfig().toAbsolutePath().toString());

        ProcessBuilder processBuilder = new ProcessBuilder(cmd)
            .directory(baseDir.toFile());
        if (logFile != null) {
            processBuilder.redirectOutput(logFile)
                .redirectError(logFile);
        } else {
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT);
        }

        LOG.info("Starting odo command {}", String.join(" ", cmd));

        Process appProcess = processBuilder.start();
        LOG.info("running odo command with pid: {}", appProcess.pid());
        appProcess.waitFor();
        LOG.info("odo exited with code: {}", appProcess.exitValue());
        if (appProcess.exitValue() != 0) {
            throw new RuntimeException("error on execution of the command '"
                + String.join(" ", cmd) + "', check log in " + logFile.getAbsolutePath().toString());
        }
    }
}
