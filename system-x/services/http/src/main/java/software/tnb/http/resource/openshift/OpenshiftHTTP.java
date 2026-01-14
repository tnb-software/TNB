package software.tnb.http.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.http.service.HTTP;

import org.apache.commons.lang3.RandomStringUtils;

import com.google.auto.service.AutoService;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.HTTPGetActionBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;

@AutoService(HTTP.class)
public class OpenshiftHTTP extends HTTP implements ReusableOpenshiftDeployable, WithName {

    private final String httpSvc = String.format("http-echo-%s", RandomStringUtils.randomAlphabetic(4).toLowerCase());
    private final String httpsSvc = String.format("https-echo-%s", RandomStringUtils.randomAlphabetic(4).toLowerCase());
    private final String name = String.format("http-echo-%s", RandomStringUtils.randomAlphabetic(4).toLowerCase());

    @Override
    public void undeploy() {
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }

    @Override
    public void create() {
        List<ContainerPort> ports = new LinkedList<>();
        ports.add(new ContainerPortBuilder()
            .withName("http")
            .withProtocol("TCP")
            .withContainerPort(HTTP_PORT)
            .build());
        ports.add(new ContainerPortBuilder()
            .withName("https")
            .withProtocol("TCP")
            .withContainerPort(HTTPS_PORT)
            .build());
        //@formatter:off

        final Probe probe = new ProbeBuilder()
            .withHttpGet(new HTTPGetActionBuilder()
                .withPort(new IntOrString(HTTP_PORT))
                .withPath("/live")
                .build()
            ).build();

        // @formatter:off
        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "env", Map.of("LOG_IGNORE_PATH", "/live"),
            "ports", ports,
            "livenessProbe", probe,
            "readinessProbe", probe
        ));

        OpenshiftClient.get().services().resource(new ServiceBuilder()
            .editOrNewMetadata()
                .withName(httpSvc)
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
            .editOrNewSpec()
                .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .addNewPort()
                    .withName("http")
                    .withProtocol("TCP")
                    .withPort(80)
                    .withTargetPort(new IntOrString(HTTP_PORT))
                .endPort()
            .endSpec()
            .build()
        ).serverSideApply();

        OpenshiftClient.get().services().resource(new ServiceBuilder()
            .editOrNewMetadata()
                .withName(httpsSvc)
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
            .editOrNewSpec()
                .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .addNewPort()
                    .withName("https")
                    .withProtocol("TCP")
                    .withPort(443)
                    .withTargetPort(new IntOrString(HTTPS_PORT))
                .endPort()
            .endSpec()
            .build()
        ).serverSideApply();
        //@formatter:on
    }

    @Override
    public boolean isDeployed() {
        return WithName.super.isDeployed();
    }

    @Override
    public Predicate<Pod> podSelector() {
        return WithName.super.podSelector();
    }

    @Override
    public String getLog() {
        return OpenshiftClient.get().getLogs(servicePod().get());
    }

    @Override
    public String getHost() {
        return OpenshiftClient.get().getClusterHostname(httpSvc);
    }

    @Override
    public int getHttpPort() {
        return 80;
    }

    @Override
    public int getHttpsPort() {
        return getHttpPort();
    }

    @Override
    public String httpUrl() {
        return "http://" + OpenshiftClient.get().getClusterHostname(httpSvc) + "/";
    }

    @Override
    public String httpsUrl() {
        return "https://" + OpenshiftClient.get().getClusterHostname(httpsSvc) + "/";
    }

    @Override
    public void cleanup() {

    }

    @Override
    public String name() {
        return name;
    }
}
