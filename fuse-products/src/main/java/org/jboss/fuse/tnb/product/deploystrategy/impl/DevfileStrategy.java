package org.jboss.fuse.tnb.product.deploystrategy.impl;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.product.deploystrategy.OpenshiftBaseDeployer;
import org.jboss.fuse.tnb.product.deploystrategy.OpenshiftDeployStrategy;
import org.jboss.fuse.tnb.product.deploystrategy.OpenshiftDeployStrategyType;
import org.jboss.fuse.tnb.product.endpoint.Endpoint;
import org.jboss.fuse.tnb.product.integration.builder.AbstractMavenGitIntegrationBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AutoService(OpenshiftDeployStrategy.class)
public class DevfileStrategy extends OpenshiftBaseDeployer {

    private static final Logger LOG = LoggerFactory.getLogger(DevfileStrategy.class);

    private static final String SB_DEVFILE = "https://raw.githubusercontent.com/mcarlett/registry/ubi/stacks/java-springboot-ubi8/devfile.yaml";

    private AbstractMavenGitIntegrationBuilder<?> gitIntegrationBuilder;

    private Path contextPath;
    private String folderName;

    @Override
    public ProductType[] products() {
        return new ProductType[]{ProductType.CAMEL_SPRINGBOOT};
    }

    @Override
    public OpenshiftDeployStrategyType deployType() {
        return OpenshiftDeployStrategyType.DEVFILE;
    }

    @Override
    public void preDeploy() {
        if (integrationBuilder instanceof AbstractMavenGitIntegrationBuilder) {
            this.gitIntegrationBuilder = (AbstractMavenGitIntegrationBuilder<?>) integrationBuilder;
        }
        if (baseDirectory.getParent().resolve("pom.xml").toFile().exists()) {
            this.contextPath = baseDirectory.getParent();
            this.folderName = gitIntegrationBuilder.getSubDirectory().get();
        } else {
            this.contextPath = baseDirectory;
            this.folderName = ".";
        }
    }

    @Override
    public void doDeploy() {
        try {
            login();
            runOdoCmd(Arrays.asList("create", "java-springboot-ubi8", "--app", name, "--context" , "."
                    , "--devfile", SB_DEVFILE)
                    , TestConfiguration.appLocation().resolve(name + "-devfile.log").toFile());

            setEnvVar("MAVEN_MIRROR_URL", TestConfiguration.mavenRepository());

            if (gitIntegrationBuilder != null && !gitIntegrationBuilder.getJavaProperties().isEmpty()) {
                String properties = gitIntegrationBuilder.getJavaProperties().entrySet().stream()
                    .map(entry -> "-D" + entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining(" "));
                setEnvVar("JAVA_OPTS_APPEND", properties);
            }

            setEnvVar("SUB_FOLDER", folderName);

            runOdoCmd(Arrays.asList("push", "--show-log", "-v", "5")
                , TestConfiguration.appLocation().resolve(name + "-deploy.log").toFile());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void undeploy() {
        try {
            login();
            final File logFile = TestConfiguration.appLocation().resolve(name + "-undeploy.log").toFile();
            runOdoCmd(Arrays.asList("delete", "--app", name, "-f"), logFile);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Endpoint getEndpoint() {
        return new Endpoint(() -> "http://" + OpenshiftClient.get().routes()
            .list().getItems()
            .stream().filter(route -> route.getMetadata().getLabels().get("app").equals(name))
            .filter(route -> route.getMetadata().getName().startsWith("http-8080"))
            .findFirst().orElseThrow(() -> new IllegalStateException("no route found"))
            .getSpec().getHost());
    }

    private void setEnvVar(String envName, String envValue) throws IOException, InterruptedException {
        runOdoCmd(Arrays.asList("config", "set", "--env", String.format("%s=%s", envName, envValue)));
    }

    private void login() throws IOException, InterruptedException {
        runOdoCmd(Arrays.asList("login", OpenshiftConfiguration.openshiftUrl(),
            String.format("--username=%s", OpenshiftConfiguration.openshiftUsername()),
            String.format("--password=%s", OpenshiftConfiguration.openshiftPassword())
        ));
        runOdoCmd(Arrays.asList("project", "set", OpenshiftConfiguration.openshiftNamespace()));
    }

    private void runOdoCmd(final List<String> args) throws IOException, InterruptedException {
        this.runOdoCmd(args, null);
    }

    private void runOdoCmd(final List<String> args, File logFile) throws IOException, InterruptedException {
        final String odoBinaryPath = System.getProperty("odo.path", IOUtils.getExecInPath("odo"));
        assert odoBinaryPath != null;

        final List<String> cmd = new ArrayList<>(args.size() + 1);
        cmd.add(odoBinaryPath);
        cmd.addAll(args);
        cmd.add("--kubeconfig");
        cmd.add(OpenshiftConfiguration.openshiftKubeconfig().toAbsolutePath().toString());

        ProcessBuilder processBuilder = new ProcessBuilder(cmd)
            .directory(contextPath.toFile());
        if (logFile != null) {
            processBuilder.redirectOutput(logFile)
                .redirectError(logFile);
        } else {
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT);
        }

        LOG.info("Starting odo command in folder {} : {}", processBuilder.directory(), String.join(" ", cmd));

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
