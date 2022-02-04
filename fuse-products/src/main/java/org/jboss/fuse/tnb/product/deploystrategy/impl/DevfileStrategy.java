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

@AutoService(OpenshiftDeployStrategy.class)
public class DevfileStrategy extends OpenshiftBaseDeployer {

    private static final Logger LOG = LoggerFactory.getLogger(DevfileStrategy.class);

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
            runOdoCmd(new String[]{"create", "java:openjdk-8-ubi8", "--s2i", "--app", name, "--context", "."
                    , "--env", String.format("MAVEN_MIRROR_URL=%s", TestConfiguration.mavenRepository())
                    , "--kubeconfig", OpenshiftConfiguration.openshiftKubeconfig().toAbsolutePath().toString()
                }
                , TestConfiguration.appLocation().resolve(name + "-devfile.log").toFile(), name);
            runOdoCmd(new String[]{"push", "--show-log", "-v", "5"}
                , TestConfiguration.appLocation().resolve(name + "-deploy.log").toFile(), name);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void undeploy(String name) {
        try {
            final File logFile = TestConfiguration.appLocation().resolve(name + "-undeploy.log").toFile();
            runOdoCmd(new String[]{"delete", "--app", name, "-f"}, logFile, name);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void runOdoCmd(String[] args, File logFile, String name) throws IOException, InterruptedException {
        String odoBinaryPath = IOUtils.getExecInPath("odo");
        Path baseDir = TestConfiguration.appLocation().resolve(name);
        String[] cmd = new String[args.length + 1];
        cmd[0] = odoBinaryPath;
        for (int i = 1; i < cmd.length; i++) {
            cmd[i] = args[i - 1];
        }
        ProcessBuilder processBuilder = new ProcessBuilder(cmd)
            .redirectOutput(logFile)
            .redirectError(logFile)
            .directory(baseDir.toFile());

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
