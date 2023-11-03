package software.tnb.jms.rabbitmq.resource.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.jms.rabbitmq.service.RabbitMQ;

import com.google.auto.service.AutoService;

import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.openshift.api.model.DeploymentConfigBuilder;

@AutoService(RabbitMQ.class)
public class OpenshiftRabbitMQBroker extends RabbitMQ implements ReusableOpenshiftDeployable, WithName, WithInClusterHostname, WithDockerImage {

    @Override
    public void undeploy() {
        OpenshiftClient.get().deploymentConfigs().withName(name()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
    }

    @Override
    public void create() {
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
                                .withImage(defaultImage())
                                .addNewPort()
                                    .withContainerPort(PORT)
                                    .withName(name())
                                .endPort()
                                .addAllToEnv(containerEnvironment().entrySet().stream().map(e -> new EnvVar(e.getKey(), e.getValue(), null))
                                    .collect(Collectors.toList()))
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
            .build());
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().apps().deployments().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).list()
            .getItems().size() > 0;
    }

    @Override
    public String name() {
        return "rabbitmq";
    }

    @Override
    public String inClusterHostname() {
        return String.format("%s.%s.svc.cluster.local", name(), OpenshiftClient.get().getNamespace());
    }

    @Override
    public Predicate<Pod> podSelector() {
        return WithName.super.podSelector();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public String getServerUrl() {
        return "tcp://" + inClusterHostname() + ":" + getPortMapping();
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }

    @Override
    public int getPortMapping() {
        return PORT;
    }
}
