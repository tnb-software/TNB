package software.tnb.product.deploystrategy.impl;

import static software.tnb.common.config.OpenshiftConfiguration.OPENSHIFT_DEPLOYMENT_LABEL;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.product.ProductType;
import software.tnb.common.utils.IOUtils;
import software.tnb.product.application.Phase;
import software.tnb.product.deploystrategy.OpenshiftBaseDeployer;
import software.tnb.product.deploystrategy.OpenshiftDeployStrategy;
import software.tnb.product.deploystrategy.OpenshiftDeployStrategyType;
import software.tnb.product.endpoint.Endpoint;
import software.tnb.product.integration.builder.AbstractMavenGitIntegrationBuilder;
import software.tnb.product.log.stream.FileLogStream;
import software.tnb.product.log.stream.LogStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.openshift.client.dsl.OpenShiftConfigAPIGroupDSL;

@AutoService(OpenshiftDeployStrategy.class)
public class DevfileStrategy extends OpenshiftBaseDeployer {

    private static final Logger LOG = LoggerFactory.getLogger(DevfileStrategy.class);

    private AbstractMavenGitIntegrationBuilder<?> gitIntegrationBuilder;
    private Path contextPath;
    private String folderName;
    private final String deploymentLabel = "application";

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
         System.setProperty(OPENSHIFT_DEPLOYMENT_LABEL, deploymentLabel);
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
        //copy resources
        copyResources(contextPath, "devfile-resources");

        try {
            copyDevfile(Paths.get(getDevfile()));
            copyDockerfile(Paths.get(getDockerfile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doDeploy() {
        try {
            login();
            List<String> envVars = new ArrayList<>();
            runOdoCmd(Arrays.asList("preference", "set", "Ephemeral", "true", "--force"), Phase.GENERATE);

            if (TestConfiguration.isMavenMirror()) {
                envVars.addAll(
                    setEnvVar("MAVEN_MIRROR_URL", StringUtils.substringBefore(TestConfiguration.mavenRepository(), "@mirrorOf"))
                );
            } else {
                envVars.addAll(
                    setEnvVar("MAVEN_MIRROR_URL", TestConfiguration.mavenRepository())
                );
            }

            envVars.addAll(setEnvVar("JAVA_OPTS_APPEND", getPropertiesForJVM(integrationBuilder)));

            envVars.addAll(setEnvVar("MAVEN_ARGS_APPEND", getPropertiesForMaven(integrationBuilder)));

            envVars.addAll(setEnvVar("SUB_FOLDER", folderName));

            envVars.addAll(setEnvVar("APP", name));

            envVars.addAll(setEnvVar("IMAGE_REGISTRY", imageURL()));

            ArrayList<String> devCommand = new ArrayList<>(List.of("deploy"));
            devCommand.addAll(envVars);

            runOdoCmd(devCommand, Phase.DEPLOY);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String getDevfile() throws IOException {
        Path tempPath = Files.createTempFile("devfile", "yaml");
        FileUtils.copyInputStreamToFile(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("devfiles/java-springboot-ubi8/devfile.yaml"), tempPath.toFile());
        return tempPath.toAbsolutePath().toString();
    }

    private String getDockerfile() throws IOException {
        Path tempPath = Files.createTempFile("Dockerfile", "");
        FileUtils.copyInputStreamToFile(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("devfiles/java-springboot-ubi8/Dockerfile"), tempPath.toFile());
        return tempPath.toAbsolutePath().toString();
    }

    @Override
    public void undeploy() {
        try {
            login();
            runOdoCmd(Arrays.asList("delete", "component", "--name", "tools"), Phase.UNDEPLOY);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Endpoint getEndpoint() {
        return new Endpoint(() -> "http://" + OpenshiftClient.get().routes()
            .list().getItems()
            .stream()
            .filter(route -> route.getMetadata().getLabels() != null)
            .filter(route -> route.getMetadata().getLabels().get(deploymentLabel) != null)
            .filter(route -> route.getMetadata().getLabels().get(deploymentLabel).equals(name))
            .filter(route -> route.getSpec().getPort().getTargetPort().equals(new IntOrString(8080)))
            .findFirst().orElseThrow(() -> new IllegalStateException("no route found"))
            .getSpec().getHost());
    }

    @Override
    public boolean isFailed() {
        return isIntegrationPodFailed();
    }

    private ArrayList<String> setEnvVar(String envName, String envValue) throws IOException, InterruptedException {
        return new ArrayList<>(List.of("--var", String.format("%s=%s", envName, envValue)));
    }

    private void copyDevfile(Path devfilePath) {
        Path resFolder = contextPath.resolve(".");
        IOUtils.copyFile(devfilePath, resFolder.resolve("devfile.yaml"));
    }

    private void copyDockerfile(Path dockerfilePath) {
        Path resFolder = contextPath.resolve(".");
        IOUtils.copyFile(dockerfilePath, resFolder.resolve("Dockerfile"));
    }

    private String imageURL() {
        return "quay.io/rh_integration/csb-ubi8:latest";
    }

    private void login() throws IOException, InterruptedException {
        final OpenShiftConfigAPIGroupDSL config = OpenshiftClient.get().config();
        final String pwd = config.getConfiguration().getPassword();

        List<String> args = new ArrayList<>();
        args.add("login");
        args.add(config.getMasterUrl().toString());

        if (pwd != null) {
            args.add(String.format("--username=%s", config.getConfiguration().getUsername()));
            args.add(String.format("--password=%s", pwd));
        } else {
            args.add(String.format("--token=%s", config.getConfiguration().getAutoOAuthToken()));
        }
        runOdoCmd(args);
        runOdoCmd(Arrays.asList("set", "namespace", OpenshiftClient.get().getNamespace()));
    }

    private void runOdoCmd(final List<String> args) throws IOException, InterruptedException {
        this.runOdoCmd(args, null);
    }

    private void runOdoCmd(final List<String> args, Phase phase) throws IOException, InterruptedException {
        final String odoBinaryPath = TestConfiguration.odoPath();
        final File logFile = TestConfiguration.appLocation().resolve(name + "-" + phase + ".log").toFile();
        final List<String> cmd = new ArrayList<>(args.size() + 1);
        LogStream logStream = null;
        cmd.add(odoBinaryPath);
        cmd.addAll(args);

        ProcessBuilder processBuilder = new ProcessBuilder(cmd)
            .directory(contextPath.toFile());
        if (Phase.DEPLOY.equals(phase)) {
            processBuilder.environment().put("ODO_IMAGE_BUILD_ARGS",
                "--build-arg=" + args.stream().filter(x -> x.contains("MAVEN_MIRROR_URL")).findFirst().get());
        }

        if (phase != null) {
            processBuilder.redirectOutput(logFile).redirectError(logFile);
            logStream = new FileLogStream(logFile.toPath(), LogStream.marker(name, phase));
        }

        LOG.debug("Starting odo command in folder {} : {}", processBuilder.directory(), String.join(" ", cmd));

        Process appProcess = processBuilder.start();
        LOG.debug("Running odo command with pid: {}", appProcess.pid());
        appProcess.waitFor();
        LOG.debug("Odo exited with code: {}", appProcess.exitValue());
        if (logStream != null) {
            logStream.stop();
        }
        if (appProcess.exitValue() != 0) {
            throw new RuntimeException("error on execution of the command '"
                + String.join(" ", cmd) + "'" + (phase != null ? ", check log in " + logFile.getAbsolutePath() : ""));
        }
    }
}
