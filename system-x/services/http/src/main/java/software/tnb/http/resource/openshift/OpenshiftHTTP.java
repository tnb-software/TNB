package software.tnb.http.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.http.service.HTTP;

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

    private static final String HTTP_SVC = "http-echo";
    private static final String HTTPS_SVC = "https-echo";

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
                .withName(HTTP_SVC)
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
                .withName(HTTPS_SVC)
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
    public String httpUrl() {
        return "http://" + OpenshiftClient.get().getClusterHostname(HTTP_SVC) + "/";
    }

    @Override
    public String httpsUrl() {
        return "https://" + OpenshiftClient.get().getClusterHostname(HTTPS_SVC) + "/";
    }

    @Override
    public void cleanup() {

    }

    @Override
    public String name() {
        return "http-echo";
    }
}
