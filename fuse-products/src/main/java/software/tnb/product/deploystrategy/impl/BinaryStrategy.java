package software.tnb.product.deploystrategy.impl;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.config.TestConfiguration;
import software.tnb.common.openshift.OpenShiftBinary;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.product.ProductType;
import software.tnb.product.application.Phase;
import software.tnb.product.csb.configuration.SpringBootConfiguration;
import software.tnb.product.deploystrategy.OpenshiftBaseDeployer;
import software.tnb.product.deploystrategy.OpenshiftDeployStrategy;
import software.tnb.product.deploystrategy.OpenshiftDeployStrategyType;
import software.tnb.product.log.stream.FileLogStream;
import software.tnb.product.log.stream.LogStream;
import software.tnb.product.util.maven.BuildRequest;
import software.tnb.product.util.maven.Maven;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import io.fabric8.openshift.client.dsl.OpenShiftConfigAPIGroupDSL;

@AutoService(OpenshiftDeployStrategy.class)
public class BinaryStrategy extends OpenshiftBaseDeployer {

    private static final Logger LOG = LoggerFactory.getLogger(BinaryStrategy.class);

    private OpenShiftBinary binary;

    @Override
    public ProductType[] products() {
        return new ProductType[] {ProductType.CAMEL_SPRINGBOOT};
    }

    @Override
    public OpenshiftDeployStrategyType deployType() {
        return OpenshiftDeployStrategyType.BINARY;
    }

    @Override
    public void preDeploy() {

        LOG.debug("copy resources to deploy from {}", baseDirectory);
        //copy resources
        copyResources(baseDirectory, "ocp/deployments/data");

        LOG.debug("build {} for OpenShift", baseDirectory);
        final Map<String, String> ompProperties = Map.of(
            "skipTests", "true"
        );
        final BuildRequest.Builder requestBuilder = new BuildRequest.Builder()
            .withBaseDirectory(baseDirectory)
            .withGoals("clean", "package")
            .withProfiles("openshift")
            .withLogFile(TestConfiguration.appLocation().resolve(name + "-build.log"))
            .withLogMarker(LogStream.marker(name, Phase.BUILD));
        Maven.invoke(requestBuilder.build());

        LOG.debug("copy generated jar");
        Arrays.stream(Objects.requireNonNull(baseDirectory.resolve("target").toFile().list(FileFilterUtils.suffixFileFilter(".jar"))))
            .findFirst().ifPresent(jarPath -> {
                try {
                    FileUtils.copyFileToDirectory(baseDirectory.resolve("target").resolve(jarPath).toFile(),
                        baseDirectory.resolve("ocp").resolve("deployments").toFile());
                } catch (IOException e) {
                    throw new RuntimeException("unable to copy jar file", e);
                }
            });

        initBinary();
    }

    @Override
    public void doDeploy() {
        final File logFile = TestConfiguration.appLocation().resolve(name + "-deploy.log").toFile();
        try (FileWriter fileWriter = new FileWriter(logFile)) {
            LogStream logStream = new FileLogStream(logFile.toPath(), LogStream.marker(name, Phase.DEPLOY));
            LOG.debug("create new build {}", name);
            fileWriter.append(binary.execute("new-build", "--binary=true"
                , String.format("--name=%s", name)
                , String.format("--image=%s", SpringBootConfiguration.openshiftBaseImage())
                , String.format("--labels=%s=%s", OpenshiftConfiguration.openshiftDeploymentLabel(), name)
            ));

            LOG.debug("start build {}", name);
            fileWriter.append(binary.execute("start-build", name
                , String.format("--from-dir=%s", baseDirectory.resolve("ocp").toAbsolutePath().toString())
                , "--follow"
            ));

            LOG.debug("generate new deployment {}", name);
            fileWriter.append(binary.execute("new-app", name, String.format("--labels=%s=%s"
                    , OpenshiftConfiguration.openshiftDeploymentLabel(), name)
                , String.format("--env=JAVA_OPTS_APPEND=%s", getPropertiesForJVM(integrationBuilder))
                , "--allow-missing-imagestream-tags=true"
            ));

            if (integrationBuilder.getPort() != 8080) {
                LOG.debug("patch service with port {}", integrationBuilder.getPort());
                fileWriter.append(binary.execute("patch", "service", name, "--type=json"
                    , "-p", "[{\"op\": \"replace\", \"path\": \"/spec/ports/0/port\", \"value\":" + integrationBuilder.getPort() + "}]"
                ));
                fileWriter.append(binary.execute("patch", "service", name, "--type=json"
                    , "-p", "[{\"op\": \"replace\", \"path\": \"/spec/ports/0/targetPort\", \"value\":" + integrationBuilder.getPort() + "}]"
                ));
                fileWriter.append(binary.execute("patch", "service", name, "--type=json"
                    , "-p", "[{\"op\": \"replace\", \"path\": \"/spec/ports/0/name\", \"value\":\"" + integrationBuilder.getPort() + "-tcp\"}]"
                ));
            }
            LOG.debug("generate route {}", name);
            fileWriter.append(binary.execute("expose", String.format("svc/%s", name)
                , String.format("--labels=%s=%s", OpenshiftConfiguration.openshiftDeploymentLabel(), name)
            ));
            logStream.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void undeploy() {
        final File logFile = TestConfiguration.appLocation().resolve(name + "-undeploy.log").toFile();
        try (FileWriter fileWriter = new FileWriter(logFile)) {
            LogStream logStream = new FileLogStream(logFile.toPath(), LogStream.marker(name, Phase.UNDEPLOY));
            fileWriter.append(binary.execute("delete", "all", "--selector", String.format("%s=%s"
                , OpenshiftConfiguration.openshiftDeploymentLabel(), name)));
            logStream.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isFailed() {
        return isIntegrationPodFailed();
    }

    private void initBinary() {
        final OpenShiftConfigAPIGroupDSL config = OpenshiftClient.get().config();
        // FIXME xtf
        binary = new OpenShiftBinary("", "");
        binary.project(config.getNamespace());
    }
}
