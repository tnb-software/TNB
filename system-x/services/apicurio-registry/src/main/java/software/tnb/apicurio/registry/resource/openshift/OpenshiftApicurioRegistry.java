package software.tnb.apicurio.registry.resource.openshift;

import software.tnb.apicurio.registry.service.ApicurioRegistry;
import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.openshift.api.model.RouteBuilder;

@AutoService(ApicurioRegistry.class)
public class OpenshiftApicurioRegistry extends ApicurioRegistry implements OpenshiftDeployable, WithName, WithInClusterHostname,
    WithExternalHostname {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftApicurioRegistry.class);

    @Override
    public void undeploy() {
        LOG.info("Undeploying OpenShift Apicurio Registry");
        LOG.debug("Deleting route {}", name());
        OpenshiftClient.get().routes().withName(name()).delete();
        LOG.debug("Deleting service {}", name());
        OpenshiftClient.get().services().withName(name()).delete();
        LOG.debug("Deleting deployment {}", name());
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        WaitUtils.waitFor(new Waiter(() -> servicePod() == null, "Waiting until the pod is removed"));
    }

    @Override
    public void create() {
        List<ContainerPort> ports = List.of(
            new ContainerPortBuilder().withName("http").withContainerPort(8080).build()
        );

        //@formatter:off
        Probe probe = new ProbeBuilder()
            .withNewTcpSocket()
                .withNewPort("http")
            .endTcpSocket()
            .withInitialDelaySeconds(0)
            .withTimeoutSeconds(5)
            .withFailureThreshold(6)
       .build();

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "ports", ports,
            "readinessProbe", probe
        ));

        OpenshiftClient.get().services().resource(
            new ServiceBuilder()
                .withNewMetadata()
                    .withName(name())
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .withNewSpec()
                    .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .addNewPort()
                        .withName("http")
                        .withPort(8080)
                        .withTargetPort(new IntOrString(8080))
                    .endPort()
                .endSpec()
                .build()
        ).serverSideApply();

        OpenshiftClient.get().routes().resource(new RouteBuilder()
            .editOrNewMetadata()
                .withName(name())
            .endMetadata()
            .editOrNewSpec()
                .withNewTo()
                    .withKind("Service")
                    .withName(name())
                    .withWeight(100)
                .endTo()
                .editOrNewPort()
                    .withTargetPort(new IntOrString(8080))
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
    public String name() {
        return "apicurio-registry";
    }

    @Override
    public Predicate<Pod> podSelector() {
        return WithName.super.podSelector();
    }

    @Override
    public String url() {
        return String.format("http://%s:8080", inClusterHostname());
    }

    @Override
    protected String clientUrl() {
        return externalHostname();
    }

    @Override
    public String externalHostname() {
        return "http://" + OpenshiftClient.get().routes().withName(name()).get().getSpec().getHost();
    }
}
