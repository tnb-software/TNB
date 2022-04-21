package org.jboss.fuse.tnb.http.resource.openshift;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.deployment.ReusableOpenshiftDeployable;
import org.jboss.fuse.tnb.common.deployment.WithName;
import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.http.service.HttpService;

import com.google.auto.service.AutoService;

import java.util.LinkedList;
import java.util.List;

import cz.xtf.core.openshift.OpenShiftWaiters;
import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HTTPGetActionBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Probe;
import io.fabric8.kubernetes.api.model.ProbeBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;

@AutoService(HttpService.class)
public class OpenshiftHttp extends HttpService implements ReusableOpenshiftDeployable, WithName {

    private static final String HTTP_SVC = "http-echo";
    private static final String HTTPS_SVC = "https-echo";

    @Override
    public void undeploy() {
        OpenshiftClient.get().deploymentConfigs().withName(name()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false)
            .areNoPodsPresent(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).timeout(120_000).waitFor();
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
                .withNewPath("/live")
                .build()
            ).build();

        OpenshiftClient.get().deploymentConfigs().createOrReplace(new DeploymentConfigBuilder()
            .withNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
                .editOrNewSpec()
                    .addToSelector(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .withReplicas(1)
                    .editOrNewTemplate()
                        .editOrNewMetadata()
                            .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                        .endMetadata()
                        .editOrNewSpec()
                            .addNewContainer()
                                .withName(name())
                                .withImage(httpImage())
                                .addAllToPorts(ports)
                                .withLivenessProbe(probe)
                                .withReadinessProbe(probe)
                                .addToEnv(new EnvVar("LOG_IGNORE_PATH", "/live", null))
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                    .addNewTrigger()
                        .withType("ConfigChange")
                    .endTrigger()
                .endSpec()
            .build());

        OpenshiftClient.get().services().createOrReplace(new ServiceBuilder()
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
        .build());

        OpenshiftClient.get().services().createOrReplace(new ServiceBuilder()
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
            .build());
        //@formatter:on
    }

    @Override
    public boolean isReady() {
        List<Pod> list = OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name());
        return ResourceFunctions.areExactlyNPodsReady(1).apply(list);
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).size() > 0 && isReady();
    }

    @Override
    public String getLog() {
        return OpenshiftClient.get().pods().withName(OpenshiftClient.get().getAnyPod(name()).getMetadata().getName()).getLog();
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
