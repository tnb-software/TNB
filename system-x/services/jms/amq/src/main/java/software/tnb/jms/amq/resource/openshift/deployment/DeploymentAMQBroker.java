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

import com.google.auto.service.AutoService;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import jakarta.jms.Connection;
import jakarta.jms.JMSException;

@AutoService(AMQBroker.class)
public class DeploymentAMQBroker extends AMQBroker implements MicroshiftDeployable, WithDockerImage, WithName, WithExternalHostname {

    private static final int PORT = 61616;
    protected static final int CONSOLE_PORT = 8161;

    @Override
    public void create() {
        List<ContainerPort> ports = List.of(
            new ContainerPortBuilder().withName(name()).withContainerPort(PORT).build()
        );

        // @formatter:off
        OpenshiftClient.get().createDeployment(Map.of(
            "name", name(),
            "image", image(),
            "env", containerEnvironment(),
            "ports", ports
        ));

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
                 .addNewPort()
                    .withName("web-console")
                    .withProtocol("TCP")
                    .withPort(CONSOLE_PORT)
                    .withTargetPort(new IntOrString(CONSOLE_PORT))
                .endPort()
                .withType("NodePort")
            .endSpec()
            .build()
        ).serverSideApply();
        // @formatter:on
    }

    @Override
    public void undeploy() {
        OpenshiftClient.get().apps().deployments().withName(name()).delete();
        OpenshiftClient.get().services().withLabel(OpenshiftConfiguration.openshiftDeploymentLabel(), name()).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until all the resources will be removed");
    }

    @Override
    public boolean isDeployed() {
        return WithName.super.isDeployed();
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
        return "registry.redhat.io/amq7/amq-broker-rhel9:7.13.3";
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
