package software.tnb.product.cq.application;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.product.application.Phase;
import software.tnb.product.cq.configuration.QuarkusConfiguration;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.customizer.component.rest.RestCustomizer;
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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.openshift.api.model.RouteBuilder;
import io.fabric8.openshift.api.model.RoutePort;

public class OpenshiftQuarkusApp extends QuarkusApp {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftQuarkusApp.class);

    public OpenshiftQuarkusApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);

        Optional<Customizer> restCustomizer = integrationBuilder.getCustomizers().stream().filter(c -> c instanceof RestCustomizer).findFirst();
        // For openshift quarkus app, the HTTP request will return default openshift 503 when the endpoint is not ready
        restCustomizer.ifPresent(customizer ->
            readinessCheck = () -> {
                final HTTPUtils.Response response =
                    HTTPUtils.getInstance().get(getEndpoint() + ((RestCustomizer) customizer).getReadinessCheckPath(), false);
                return response.getResponseCode() != 503 && response.getResponseCode() != 0;
            });
    }

    @Override
    public void start() {
        logCounter++;
        LOG.trace("Creating service account for the integration {}", getName());
        ServiceAccount sa = new ServiceAccountBuilder().withNewMetadata().withName(getName()).endMetadata().build();
        OpenshiftClient.get().serviceAccounts().resource(sa).serverSideApply();

        final BuildRequest.Builder builder = new BuildRequest.Builder()
            .withBaseDirectory(TestConfiguration.appLocation().resolve(getName()))
            .withArgs("package")
            .withProperties(getProperties())
            .withLogFile(getLogPath(Phase.DEPLOY))
            .withLogMarker(LogStream.marker(getName(), Phase.DEPLOY));
        if (QuarkusConfiguration.isQuarkusNative()) {
            builder.withProfiles("native");
        }
        Maven.invoke(builder.build());

        // @formatter:off
        if (OpenshiftClient.get().services().withName(getName()).get() == null) {
            // create the service and route manually if it is not created by quarkus
            final ServiceBuilder serviceBuilder = new ServiceBuilder()
                .withNewMetadata()
                    .withName(getName())
                    .addToLabels("app.kubernetes.io/name", getName())
                .endMetadata()
                .withNewSpec()
                    .addToSelector("app.kubernetes.io/name", getName())
                    .addNewPort()
                        .withName("port")
                        .withPort(integrationBuilder.getPort())
                        .withProtocol("TCP")
                    .endPort()
                .endSpec();

            integrationBuilder.getAdditionalPorts().forEach(port ->
                serviceBuilder.editSpec()
                    .addNewPort()
                        .withName("port-" + port)
                        .withPort(port)
                        .withProtocol("TCP")
                    .endPort()
                .endSpec());

            OpenshiftClient.get().services().resource(serviceBuilder.build()).serverSideApply();
        }
        if (OpenshiftClient.get().routes().withName(getName()).get() == null) {
            OpenshiftClient.get().routes().resource(new RouteBuilder()
                .withNewMetadata()
                    .withName(getName())
                    .addToLabels("app.kubernetes.io/name", getName())
                .endMetadata()
                .withNewSpec()
                    .withPort(new RoutePort(new IntOrString(integrationBuilder.getPort())))
                    .withNewTo()
                        .withKind("Service")
                        .withName(getName())
                        .withWeight(100)
                    .endTo()
                .endSpec()
                .build()
            ).serverSideApply();
        }
        // @formatter:on

        endpoint = new Endpoint(() -> "http://" + OpenshiftClient.get().routes().withName(getName()).get().getSpec().getHost());

        Predicate<Pod> podSelector = p -> p.getMetadata().getLabels() != null && p.getMetadata().getLabels().containsKey("app.kubernetes.io/name")
            && getName().equals(p.getMetadata().getLabels().get("app.kubernetes.io/name"));
        log = new OpenshiftLog(podSelector, getLogPath());
        logStream = new OpenshiftLogStream(podSelector, LogStream.marker(getName()));
    }

    private Map<String, String> getProperties() {
        final Map<String, String> properties = new HashMap<>(Map.of(
            "quarkus.kubernetes-client.master-url", OpenshiftClient.get().getConfiguration().getMasterUrl(),
            "quarkus.kubernetes-client.token", OpenshiftClient.get().getConfiguration().getAutoOAuthToken(),
            "quarkus.kubernetes-client.namespace", OpenshiftClient.get().getNamespace(),
            "quarkus.kubernetes-client.trust-certs", "true",
            "quarkus.kubernetes.deploy", "true",
            "quarkus.native.container-build", "true",
            "quarkus.openshift.build-strategy", "docker",
            "quarkus.openshift.service-account", getName(),
            "skipTests", "true"
        ));
        if (!QuarkusConfiguration.isQuarkusNative()) {
            properties.put("quarkus.openshift.command", getJavaCommand());
        }
        properties.putAll(QuarkusConfiguration.fromSystemProperties());
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
        return String.join(",", String.format(defaultCmd, String.join(" ", systemProperties())).split(" "));
    }

    @Override
    public void stop() {
        super.stop();

        LOG.info("Undeploying integration resources");
        OpenshiftClient.get().routes().withName(getName()).delete();
        OpenshiftClient.get().services().withName(getName()).delete();
        OpenshiftClient.get().serviceAccounts().withName(getName()).delete();
        Path openshiftResources = TestConfiguration.appLocation().resolve(getName()).resolve("target").resolve("kubernetes/openshift.yml");

        try (InputStream is = IOUtils.toInputStream(Files.readString(openshiftResources), "UTF-8")) {
            LOG.info("Deleting openshift resources for integration from file {}", openshiftResources.toAbsolutePath());
            OpenshiftClient.get().load(is).get().stream().filter(Objects::nonNull).forEach(item -> OpenshiftClient.get().resource(item).delete());
        } catch (IOException e) {
            throw new RuntimeException("Unable to read openshift.yml resource: ", e);
        }
    }

    @Override
    public void kill() {
        LOG.warn("kill() is not supported on OpenShift, calling stop()");
        stop();
    }

    @Override
    public boolean isReady() {
        return ResourceFunctions.areExactlyNPodsReady(1).apply(OpenshiftClient.get().getLabeledPods("app.kubernetes.io/name", getName()));
    }

    @Override
    public boolean isFailed() {
        return deployPodFailed() || integrationPodFailed();
    }

    private boolean deployPodFailed() {
        long lastVersion;
        try {
            lastVersion = OpenshiftClient.get().buildConfigs().withName(getName()).get().getStatus().getLastVersion();
            return OpenshiftClient.get()
                .isPodFailed(OpenshiftClient.get().getLabeledPods("openshift.io/deployer-pod-for.name", getName() + "-" + lastVersion).get(0));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean integrationPodFailed() {
        final List<Pod> pods = OpenshiftClient.get().getLabeledPods("app.kubernetes.io/name", getName());
        if (pods.isEmpty()) {
            return false;
        } else {
            return OpenshiftClient.get().isPodFailed(OpenshiftClient.get().getLabeledPods("app.kubernetes.io/name", getName()).get(0));
        }
    }
}
