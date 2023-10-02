package software.tnb.aws.s3.service.openshift;

import software.tnb.aws.s3.service.Minio;
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
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.PortForward;
import io.fabric8.kubernetes.client.dsl.PodResource;

@AutoService(Minio.class)
public class OpenshiftMinio extends Minio implements OpenshiftDeployable, WithName, WithInClusterHostname, WithExternalHostname {

    private PortForward portForward;
    private int localPort;

    @Override
    public void create() {

        List<ContainerPort> ports = new LinkedList<>();
        ports.add(new ContainerPortBuilder()
            .withName("api")
            .withContainerPort(CONTAINER_API_PORT)
            .withProtocol("TCP").build());
        ports.add(new ContainerPortBuilder()
            .withName("ui")
            .withContainerPort(CONTAINER_UI_PORT)
            .withProtocol("TCP").build());

        OpenshiftClient.get().apps().deployments().createOrReplace(
            new DeploymentBuilder()
                .editOrNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .editOrNewSpec()
                .editOrNewSelector()
                .addToMatchLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endSelector()
                .withReplicas(1)
                .editOrNewTemplate()
                .editOrNewMetadata()
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .editOrNewSpec()
                .addNewVolume()
                .withName("data").withNewEmptyDir().endEmptyDir()
                .endVolume()
                .addNewContainer()
                .withName(name()).withImage(image()).withPorts(ports).withEnv(
                    new EnvVar("MINIO_ROOT_USER", account().accountId(), null),
                    new EnvVar("MINIO_ROOT_PASSWORD", account().secretKey(), null)
                ).withArgs("server", "/data", "--console-address", ":" + CONTAINER_UI_PORT)
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec()
                .build()
        );

        ServiceSpecBuilder serviceSpecBuilder = new ServiceSpecBuilder().addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name());
        serviceSpecBuilder.addToPorts(new ServicePortBuilder()
            .withName(name())
            .withPort(CONTAINER_API_PORT)
            .withTargetPort(new IntOrString(CONTAINER_API_PORT))
            .build());
        OpenshiftClient.get().services().createOrReplace(
            new ServiceBuilder()
                .editOrNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .endMetadata()
                .editOrNewSpecLike(serviceSpecBuilder.build())
                .endSpec()
                .build()
        );
    }

    @Override
    public void undeploy() {
        OpenshiftClient.get().routes().withName(name()).delete();
        OpenshiftClient.get().services().withName(name()).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenshiftClient.get().persistentVolumeClaims().withName(name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void openResources() {
        localPort = NetworkUtils.getFreePort();
        portForward = OpenshiftClient.get().services().withName(name()).portForward(CONTAINER_API_PORT, localPort);
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
        return pod != null && pod.isReady() && OpenshiftClient.get().getLogs(pod.get()).contains("1 Online, 0 Offline.");
    }

    @Override
    public boolean isDeployed() {
        Deployment deployment = OpenshiftClient.get().apps().deployments().withName(name()).get();
        return deployment != null && !deployment.isMarkedForDeletion();
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
        return "minio";
    }

    @Override
    public String externalHostname() {
        return "localhost";
    }

    @Override
    public String hostname() {
        return String.format("http://%s:%d", inClusterHostname(), CONTAINER_API_PORT);
    }

    @Override
    protected String clientHostname() {
        return String.format("http://%s:%d", externalHostname(), localPort);
    }
}
