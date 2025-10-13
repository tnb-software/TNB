package software.tnb.product.csb.application;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.product.deploystrategy.OpenshiftDeployStrategyFactory;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.integration.builder.AbstractMavenGitIntegrationBuilder;
import software.tnb.product.interfaces.OpenshiftDeployer;
import software.tnb.product.log.stream.LogStream;
import software.tnb.product.log.stream.OpenshiftLogStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.readiness.Readiness;

public class OpenshiftSpringBootApp extends SpringBootApp {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftSpringBootApp.class);
    private final String finalName;
    private final OpenshiftDeployer deploymentStrategy;

    public OpenshiftSpringBootApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);
        deploymentStrategy = OpenshiftDeployStrategyFactory.getDeployStrategy(integrationBuilder);

        Path baseDirectory;
        if (integrationBuilder instanceof AbstractMavenGitIntegrationBuilder<?> mavenGitIntegrationBuilder) {
            baseDirectory = mavenGitApp != null ? mavenGitApp.getProjectLocation() : TestConfiguration.appLocation().resolve(getName());
            finalName = mavenGitIntegrationBuilder.getFinalName().orElse(getName());
        } else {
            baseDirectory = TestConfiguration.appLocation().resolve(getName());
            finalName = getName();
        }
        deploymentStrategy
            .setBaseDirectory(baseDirectory)
            .setName(finalName);
    }

    @Override
    public void start() {
        logCounter++;
        LOG.info("Deploy app using {}", deploymentStrategy.getClass().getSimpleName());
        deploymentStrategy.deploy();
        endpoint = deploymentStrategy.getEndpoint();
        log = deploymentStrategy.getLog(getLogPath());
        logStream = new OpenshiftLogStream(deploymentStrategy.podSelector(), LogStream.marker(finalName));
    }

    @Override
    public void stop() {
        super.stop();

        deploymentStrategy.undeploy();
    }

    @Override
    public void kill() {
        LOG.warn("kill() is not supported on OpenShift, calling stop()");
        stop();
    }

    @Override
    public boolean isReady() {
        try {
            final List<Pod> pods = OpenshiftClient.get().getPods().stream().filter(deploymentStrategy.podSelector()).toList();
            return !pods.isEmpty() && pods.stream()
                .filter(pod -> !pod.isMarkedForDeletion())
                .filter(pod -> !"Evicted".equals(pod.getStatus().getReason()))
                .allMatch(Readiness::isPodReady);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean isFailed() {
        return deploymentStrategy.isFailed();
    }
}
