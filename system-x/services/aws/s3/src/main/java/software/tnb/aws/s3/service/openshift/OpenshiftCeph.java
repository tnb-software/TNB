package software.tnb.aws.s3.service.openshift;

import software.tnb.aws.s3.service.Ceph;
import software.tnb.aws.s3.validation.S3Validation;
import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.IOUtils;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.common.utils.WaitUtils;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.TCPSocketActionBuilder;
import io.fabric8.kubernetes.client.PortForward;
import io.fabric8.kubernetes.client.dsl.PodResource;

@AutoService(Ceph.class)
public class OpenshiftCeph extends Ceph implements OpenshiftDeployable, WithName, WithInClusterHostname, WithExternalHostname {

    private PortForward portForward;
    private int localPort;
    private String sccName;
    private String serviceAccountName;

    @Override
    public void create() {
        sccName = "tnb-ceph-" + OpenshiftClient.get().getNamespace();

        serviceAccountName = name() + "-sa";

        // @formatter:off
        OpenshiftClient.get().serviceAccounts().resource(new ServiceAccountBuilder()
            .withNewMetadata()
                .withName(serviceAccountName)
            .endMetadata()
            .build()
        ).serverSideApply();

        OpenshiftClient.get().addUsersToSecurityContext(
            OpenshiftClient.get().createSecurityContext(sccName, "anyuid", "SYS_CHROOT"),
            OpenshiftClient.get().getServiceAccountRef(serviceAccountName)
        );

        List<ContainerPort> ports = new LinkedList<>();
        ports.add(new ContainerPortBuilder()
            .withName("api")
            .withContainerPort(CONTAINER_PORT)
            .withProtocol("TCP").build()
        );

        final Probe probe = new ProbeBuilder()
            .withTcpSocket(new TCPSocketActionBuilder()
                .withPort(new IntOrString(CONTAINER_PORT))
                .build()
            )
            .withInitialDelaySeconds(5)
            .withTimeoutSeconds(2)
            .withFailureThreshold(5)
            .build();

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "env", environment(),
            "serviceAccount", serviceAccountName,
            "scc", sccName,
            "ports", ports,
            "readinessProbe", probe,
            "livenessProbe", probe
        ));

        OpenshiftClient.get().services().resource(
            new ServiceBuilder()
                .editOrNewMetadata()
                    .withName(name())
                    .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .editOrNewSpec()
                    .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .addToPorts(new ServicePortBuilder()
                        .withName(name())
                        .withPort(CONTAINER_PORT)
                        .withTargetPort(new IntOrString(CONTAINER_PORT))
                        .build()
                    )
                .endSpec()
                .build()
        ).serverSideApply();
        // @formatter:on
    }

    @Override
    public void undeploy() {
        OpenshiftClient.get().routes().withName(name()).delete();
        OpenshiftClient.get().services().withName(name()).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenshiftClient.get().persistentVolumeClaims().withName(name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");

        OpenshiftClient.get().securityContextConstraints().withName(sccName).delete();
        OpenshiftClient.get().serviceAccounts().withName(serviceAccountName).delete();
    }

    @Override
    public void openResources() {
        localPort = NetworkUtils.getFreePort();
        portForward = OpenshiftClient.get().services().withName(name()).portForward(CONTAINER_PORT, localPort);
        validation = new S3Validation(client());
    }

    @Override
    public void closeResources() {
        client = null;
        IOUtils.closeQuietly(portForward);
        NetworkUtils.releasePort(localPort);
    }

    @Override
    public boolean isReady() {
        final PodResource pod = servicePod();
        return pod != null && pod.isReady();
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
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        OpenshiftDeployable.super.beforeAll(extensionContext);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        super.afterAll(extensionContext);
        OpenshiftDeployable.super.afterAll(extensionContext);
    }

    @Override
    public String name() {
        return "ceph";
    }

    @Override
    public String externalHostname() {
        return "localhost";
    }

    @Override
    public String hostname() {
        // Use IP here so that the S3 client uses path-style URL (http://svc/bucket) and not virtual host style (http://bucket.svc) as that does not
        // work in openshift
        return OpenshiftClient.get().services().withName(name()).getURL(name()).replace("tcp", "http");
    }

    @Override
    public String clientHostname() {
        return "http://localhost:" + localPort;
    }
}
