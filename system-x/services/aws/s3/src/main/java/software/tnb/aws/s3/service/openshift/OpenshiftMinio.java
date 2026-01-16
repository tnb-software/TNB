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
import software.tnb.common.utils.waiter.Waiter;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.TCPSocketActionBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.client.PortForward;
import io.fabric8.kubernetes.client.dsl.PodResource;

@AutoService(Minio.class)
public class OpenshiftMinio extends Minio implements OpenshiftDeployable, WithName, WithInClusterHostname, WithExternalHostname {

    private PortForward portForward;
    private int localPort;

    @Override
    public void create() {
        // @formatter:off
        final Probe probe = new ProbeBuilder()
            .withTcpSocket(new TCPSocketActionBuilder()
                .withPort(new IntOrString(CONTAINER_API_PORT))
                .build()
            )
            .withInitialDelaySeconds(5)
            .withTimeoutSeconds(2)
            .withFailureThreshold(5)
            .build();

        List<String> args = List.of("server", "/data", "--console-address", ":" + CONTAINER_UI_PORT);
        List<Volume> volumes = List.of(
            new VolumeBuilder().withNewEmptyDir().endEmptyDir().withName("data").build()
        );
        List<VolumeMount> volumeMounts = List.of(
            new VolumeMountBuilder().withMountPath("/data").withName("data").build()
        );

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "env", containerEnvironment(),
            "args", args,
            "volumes", volumes,
            "volumeMounts", volumeMounts,
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
                        .withPort(CONTAINER_API_PORT)
                        .withTargetPort(new IntOrString(CONTAINER_API_PORT))
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
        WaitUtils.waitFor(new Waiter(() -> servicePod() == null, "Waiting until the pod is removed"));
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
