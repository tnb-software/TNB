package software.tnb.jms.amq.resource.openshift.deployment;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.MicroshiftDeployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.jms.amq.service.AMQBroker;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import jakarta.jms.Connection;
import jakarta.jms.JMSException;

@AutoService(AMQBroker.class)
public class DeploymentAMQBroker extends AMQBroker implements MicroshiftDeployable, WithDockerImage, WithName, WithExternalHostname {

    private static final Logger LOG = LoggerFactory.getLogger(DeploymentAMQBroker.class);

    private static final int PORT = 61616;
    protected static final int CONSOLE_PORT = 8161;

    @Override
    public void create() {
        OpenshiftClient.get().apps().deployments().createOrReplace(new DeploymentBuilder()
            .withNewMetadata()
                .withName(name())
                .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
            .endMetadata()
                .editOrNewSpec()
                    .withNewSelector()
                        .addToMatchLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                    .endSelector()
                    .withReplicas(1)
                    .editOrNewTemplate()
                        .editOrNewMetadata()
                            .addToLabels(OpenshiftConfiguration.openshiftDeploymentLabel(), name())
                        .endMetadata()
                        .editOrNewSpec()
                            .addNewContainer()
                                .withName(name())
                                .withImage(image())
                                .addNewPort()
                                    .withContainerPort(PORT)
                                    .withName(name())
                                .endPort()
                                .addAllToEnv(containerEnvironment().entrySet().stream().map(e -> new EnvVar(e.getKey(), e.getValue(), null))
                                    .collect(Collectors.toList()))
                            .endContainer()
                        .endSpec()
                    .endTemplate()
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
                 .addNewPort()
                    .withName("web-console")
                    .withProtocol("TCP")
                    .withPort(CONSOLE_PORT)
                    .withTargetPort(new IntOrString(CONSOLE_PORT))
                .endPort()
                .withType("NodePort")
            .endSpec()
            .build());
    }

    @Override
    public void undeploy() {
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until all the resources will be removed");
    }

    @Override
    public boolean isDeployed() {
        return !OpenshiftClient.get().apps().deployments().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).list()
            .getItems().isEmpty();
    }

    @Override
    public String name() {
        return "amq-broker";
    }

    @Override
    public Predicate<Pod> podSelector() {
        return WithName.super.podSelector();
    }

    @Override
    public String defaultImage() {
        return "registry.redhat.io/amq7/amq-broker-rhel8:7.11.0";
    }

    @Override
    public String externalHostname() {
        return OpenshiftClient.get().config().getMasterUrl().getHost();
    }

    @Override
    public String host() {
        return name();
    }

    @Override
    public int getPortMapping(int port) {
        //if the external nodeport il configured,
        return PORT;
    }

    @Override
    public String mqttUrl() {
        return String.format("tcp://%s:%d", name(), getPortMapping(1883));
    }

    @Override
    protected String mqttClientUrl() {
        return "tcp://" + externalBrokerConnectionUrl();
    }

    @Override
    public String amqpUrl() {
        return String.format("amqp://%s:%d", name(), getPortMapping(5672));
    }

    private String externalBrokerConnectionUrl() {
        final Integer nodePort = OpenshiftClient.get().getService(name()).getSpec().getPorts().stream().findFirst().get().getNodePort();
        return externalHostname() + ":" + nodePort;
    }

    @Override
    protected Connection createConnection() {
        try {

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(String
                .format("tcp://%s?ha=true&reconnectAttempts=3&initialConnectAttempts=5", externalBrokerConnectionUrl())
                , account().username(), account().password());

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            return connection;
        } catch (JMSException e) {
            throw new RuntimeException("Can't create jms connection", e);
        }
    }
}
