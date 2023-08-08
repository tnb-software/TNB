package software.tnb.product.cq.application;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.product.application.Phase;
import software.tnb.product.cq.OpenshiftCamelQuarkus;
import software.tnb.product.cq.configuration.QuarkusConfiguration;
import software.tnb.product.endpoint.Endpoint;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.log.OpenshiftLog;
import software.tnb.product.log.stream.LogStream;
import software.tnb.product.log.stream.OpenshiftLogStream;
import software.tnb.product.util.maven.BuildRequest;
import software.tnb.product.util.maven.Maven;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.api.model.RoutePort;

public class OpenshiftQuarkusApp extends QuarkusApp {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftCamelQuarkus.class);

    private final AbstractIntegrationBuilder<?> integrationBuilder;

    public OpenshiftQuarkusApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);
        this.integrationBuilder = integrationBuilder;
    }

    @Override
    public void start() {
        final BuildRequest.Builder builder = new BuildRequest.Builder()
            .withBaseDirectory(TestConfiguration.appLocation().resolve(name))
            .withGoals("package")
            .withProperties(getProperties())
            .withLogFile(getLogPath(Phase.DEPLOY))
            .withLogMarker(LogStream.marker(name, Phase.DEPLOY));
        if (QuarkusConfiguration.isQuarkusNative()) {
            builder.withProfiles("native");
        }
        Maven.invoke(builder.build());

        // @formatter:off
        if (OpenshiftClient.get().services().withName(name).get() == null) {
            // create the service and route manually if it is not created by quarkus
            OpenshiftClient.get().services().createOrReplace(new ServiceBuilder()
                .withNewMetadata()
                    .withName(name)
                    .addToLabels("app.kubernetes.io/name", name)
                .endMetadata()
                .withNewSpec()
                    .addToSelector("app.kubernetes.io/name", name)
                    .addNewPort()
                        .withName("port")
                        .withPort(integrationBuilder.getPort())
                        .withProtocol("TCP")
                    .endPort()
                .endSpec()
                .build()
            );
        }
        if (OpenshiftClient.get().routes().withName(name).get() == null) {
            OpenshiftClient.get().routes().createOrReplace(new RouteBuilder()
                .withNewMetadata()
                    .withName(name)
                    .addToLabels("app.kubernetes.io/name", name)
                .endMetadata()
                .withNewSpec()
                    .withPort(new RoutePort(new IntOrString(integrationBuilder.getPort())))
                    .withNewTo()
                        .withKind("Service")
                        .withName(name)
                        .withWeight(100)
                    .endTo()
                .endSpec()
                .build()
            );
        }
        // @formatter:on

        endpoint = new Endpoint(() -> "http://" + OpenshiftClient.get().routes().withName(name).get().getSpec().getHost());

        Predicate<Pod> podSelector = p -> p.getMetadata().getLabels().containsKey("app.kubernetes.io/name")
            && name.equals(p.getMetadata().getLabels().get("app.kubernetes.io/name"));
        log = new OpenshiftLog(podSelector, getLogPath());
        logStream = new OpenshiftLogStream(podSelector, LogStream.marker(name));
    }

    private Map<String, String> getProperties() {
        final Map<String, String> properties = new HashMap<>(Map.of(
            "quarkus.kubernetes-client.master-url", OpenshiftClient.get().getConfiguration().getMasterUrl(),
            "quarkus.kubernetes-client.token", OpenshiftClient.get().getConfiguration().getOauthToken(),
            "quarkus.kubernetes-client.namespace", OpenshiftClient.get().getNamespace(),
            "quarkus.kubernetes-client.trust-certs", "true",
            "quarkus.kubernetes.deploy", "true",
            "quarkus.native.container-build", "true",
            "skipTests", "true"
        ));
        if (!QuarkusConfiguration.isQuarkusNative()) {
            properties.put("quarkus.openshift.command", getJavaCommand());
        }
        return properties;
    }

    /**
     * Return the java command to run into the container
     *
     * @return String, the command for running java app in the container, all parameters must be comma separated
     */
    private String getJavaCommand() {
        final String defaultCmd = "java %s -Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager "
            + "-jar /deployments/quarkus-run.jar";
        return String.join(",", String.format(defaultCmd, Optional.ofNullable(this.integrationBuilder.getProperties())
                .orElse(new Properties()).entrySet().stream().map(e -> "-D" + e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(" ")))
            .split(" "));
    }

    @Override
    public void stop() {
        if (logStream != null) {
            logStream.stop();
        }
        if (getLog() != null) {
            ((OpenshiftLog) getLog()).save(started);
        }
        LOG.info("Undeploying integration resources");
        OpenshiftClient.get().routes().withName(name).delete();
        OpenshiftClient.get().services().withName(name).delete();
        Path openshiftResources = TestConfiguration.appLocation().resolve(name).resolve("target").resolve("kubernetes/openshift.yml");

        try (InputStream is = IOUtils.toInputStream(Files.readString(openshiftResources), "UTF-8")) {
            LOG.info("Deleting openshift resources for integration from file {}", openshiftResources.toAbsolutePath());
            OpenshiftClient.get().load(is).get().forEach(item -> OpenshiftClient.get().resource(item).delete());
        } catch (IOException e) {
            throw new RuntimeException("Unable to read openshift.yml resource: ", e);
        }
    }

    @Override
    public boolean isReady() {
        return ResourceFunctions.areExactlyNPodsRunning(1).apply(OpenshiftClient.get().getLabeledPods("app.kubernetes.io/name", name));
    }

    @Override
    public boolean isFailed() {
        return deployPodFailed() || integrationPodFailed();
    }

    private boolean deployPodFailed() {
        long lastVersion;
        try {
            lastVersion = OpenshiftClient.get().buildConfigs().withName(name).get().getStatus().getLastVersion();
            return OpenshiftClient.get()
                .isPodFailed(OpenshiftClient.get().getLabeledPods("openshift.io/deployer-pod-for.name", name + "-" + lastVersion).get(0));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean integrationPodFailed() {
        final List<Pod> pods = OpenshiftClient.get().getLabeledPods("app.kubernetes.io/name", name);
        if (pods.size() == 0) {
            return false;
        } else {
            return OpenshiftClient.get().isPodFailed(OpenshiftClient.get().getLabeledPods("app.kubernetes.io/name", name).get(0));
        }
    }
}
