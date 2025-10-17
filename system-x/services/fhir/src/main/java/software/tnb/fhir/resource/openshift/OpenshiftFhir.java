package software.tnb.fhir.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.StringUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.fhir.service.Fhir;

import org.junit.jupiter.api.BeforeAll;

import org.apache.commons.lang3.RandomStringUtils;

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
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.openshift.api.model.RouteBuilder;

@AutoService(Fhir.class)
public class OpenshiftFhir extends Fhir implements ReusableOpenshiftDeployable, WithName, WithExternalHostname {

    static String sccName = String.format("tnb-fhir-%s", RandomStringUtils.randomAlphabetic(4).toLowerCase());
    static String serviceAccountName = "fhir-sa";

    @BeforeAll
    public static void initOCPServiceAccount() {
        OpenshiftClient.get().addUsersToSecurityContext(
            OpenshiftClient.get().createSecurityContext(sccName, "anyuid", "SYS_CHROOT"),
            OpenshiftClient.get().getServiceAccountRef(serviceAccountName)
        );
    }

    @Override
    public void undeploy() {
        OpenshiftClient.get().securityContextConstraints().withName(sccName).delete();
        OpenshiftClient.get().serviceAccounts().withName(serviceAccountName).delete();
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        OpenshiftClient.get().routes().withName(name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void create() {
        final String authToken = StringUtils.base64Encode(account().username() + ":" + account().password());

        if (OpenshiftClient.get().routes().withName(name()).get() == null) {
            // @formatter:off
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
                        .withTargetPort(new IntOrString("fhir"))
                    .endPort()
                    .withNewTls()
                        .withTermination("edge")
                        .withInsecureEdgeTerminationPolicy("Allow")
                    .endTls()
                     .withWildcardPolicy("None")
                .endSpec()
                .build()).create();
            // @formatter:on
        }

        List<ContainerPort> ports = List.of(
            new ContainerPortBuilder().withName(name()).withContainerPort(PORT).build()
        );

        // @formatter:off
        OpenshiftClient.get().serviceAccounts().resource(new ServiceAccountBuilder()
            .withNewMetadata()
            .withName(serviceAccountName)
            .endMetadata()
            .build()
        ).serverSideApply();

        final Probe probe = new ProbeBuilder()
            .editOrNewExec()
            .withCommand("curl", "http://0.0.0.0:8080/fhir/metadata",
                "--header", "\"Authorization: Basic " + authToken + "\"")
            .endExec()
            .withInitialDelaySeconds(60)
            .withTimeoutSeconds(5)
            .withFailureThreshold(10)
            .build();

        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "env", containerEnvironment(),
            "serviceAccount", serviceAccountName,
            "securityContext", "runAsUser: 100",
            "ports", ports,
            "readinessProbe", probe,
            "livenessProbe", probe
        ));

        // @formatter:off
        OpenshiftClient.get().services().resource(new ServiceBuilder()
            .editOrNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
            .editOrNewSpec()
                .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                .addNewPort()
                    .withName(name())
                    .withProtocol("TCP")
                    .withPort(PORT)
                    .withTargetPort(new IntOrString(PORT))
                .endPort()
            .endSpec()
            .build()
        ).serverSideApply();
        // @formatter:on
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
    public void cleanup() {

    }

    @Override
    public int getPortMapping() {
        return PORT;
    }

    @Override
    public String getServerUrl() {
        return String.format("http://%s/fhir/", externalHostname());
    }

    @Override
    public String name() {
        return "fhir";
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }

    @Override
    public String externalHostname() {
        return OpenshiftClient.get().routes().withName(name()).get().getSpec().getHost();
    }
}
