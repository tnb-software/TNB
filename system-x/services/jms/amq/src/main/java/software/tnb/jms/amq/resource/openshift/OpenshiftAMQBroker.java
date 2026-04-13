package software.tnb.jms.amq.resource.openshift;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;
import software.tnb.jms.amq.resource.openshift.generated.ActiveMQArtemis;
import software.tnb.jms.amq.resource.openshift.generated.ActiveMQArtemisList;
import software.tnb.jms.amq.resource.openshift.generated.ActiveMQArtemisSpec;
import software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.Acceptors;
import software.tnb.jms.amq.resource.openshift.generated.activemqartemisspec.DeploymentPlan;
import software.tnb.jms.amq.service.AMQBroker;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import cz.xtf.core.openshift.helpers.ResourceParsers;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.openshift.api.model.Route;
import jakarta.jms.Connection;
import jakarta.jms.JMSException;

@AutoService(AMQBroker.class)
public class OpenshiftAMQBroker extends AMQBroker implements OpenshiftDeployable, WithInClusterHostname, WithExternalHostname, WithOperatorHub,
    WithName {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftAMQBroker.class);
    private static final String SSL_SECRET_NAME = "tnb-ssl-secret";
    public static final String OPERATOR_NAME = "amq-broker-rhel9";
    public static final String OPERATOR_CHANNEL = "7.13.x";

    @Override
    public void create() {
        LOG.debug("Creating AMQ broker");
        // Create subscription for amq broker operator
        createSubscription();
        // Create amq-broker custom resource
        OpenshiftClient.get().resources(ActiveMQArtemis.class, ActiveMQArtemisList.class).resource(createBrokerCR()).create();
    }

    @Override
    public boolean isDeployed() {
        List<Pod> pods = OpenshiftClient.get().getLabeledPods("name", "amq-broker-operator");
        return pods.size() == 1 && ResourceParsers.isPodReady(pods.get(0)) && !amqBrokerClient().list().getItems().isEmpty();
    }

    @Override
    public Predicate<Pod> podSelector() {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of("ActiveMQArtemis", name()));
    }

    @Override
    public String inClusterHostname() {
        return String.format("%s.%s-hdls-svc.%s.svc.cluster.local", servicePod().get().getMetadata().getName(), name(),
            OpenshiftClient.get().getNamespace());
    }

    @Override
    public String externalHostname() {
        final List<Route> routes = OpenshiftClient.get().routes().withLabel("ActiveMQArtemis", name()).list().getItems();

        if (routes.size() != 1) {
            throw new RuntimeException("Expected single route to be present but was " + routes.size());
        }

        return routes.get(0).getSpec().getHost();
    }

    @Override
    public void undeploy() {
        amqBrokerClient().withName(name()).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        WaitUtils.waitFor(() -> servicePod() == null, "Waiting until the pod is removed");
        OpenshiftClient.get().deleteSecret(SSL_SECRET_NAME);
        deleteSubscription(() -> OpenshiftClient.get().getLabeledPods("name", "amq-broker-operator").isEmpty());
    }

    @Override
    protected String mqttClientUrl() {
        return String.format("ssl://%s:443", externalHostname());
    }

    @Override
    public String host() {
        return inClusterHostname();
    }

    @Override
    public int getPortMapping(int port) {
        //redirect all to the internal acceptor
        return 61626;
    }

    @Override
    protected Connection createConnection() {
        try {
            final String tsPath = materializeTrustStore().toAbsolutePath().toString();
            // Set the amq related properties for truststore and don't use truststore as part of the broker url
            // on jenkins, the mvn is invoked with -Djavax.net.ssl.trustStore=... -Djavax.net.ssl.trustStorePassword=... due to nexus https
            // and those takes precedence over whatever is used in the broker url
            System.setProperty("org.apache.activemq.ssl.trustStore", tsPath);
            System.setProperty("org.apache.activemq.ssl.trustStorePassword", account().truststorePassword());

            // use route for external clients
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(String.format(
                "tcp://%s:%s?useTopologyForLoadBalancing=false&sslEnabled=true&verifyHost=false", externalHostname(), 443), account().username(),
                account().password()
            );

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            return connection;
        } catch (JMSException e) {
            throw new RuntimeException("Can't create jms connection", e);
        } catch (IOException e) {
            throw new RuntimeException("Can't materialize jms truststore", e);
        }
    }

    private ActiveMQArtemis createBrokerCR() {
        final Map<String, String> data = Map.of(
            "keyStorePassword", encode(account().keystorePassword().getBytes()),
            "trustStorePassword", encode(account().truststorePassword().getBytes()),
            "client.ts", encodeResource("client.ts"),
            "broker.ks", encodeResource("broker.ks")
        );

        SecretBuilder sb = new SecretBuilder().editOrNewMetadata().withName(SSL_SECRET_NAME).endMetadata().withData(data);
        OpenshiftClient.get().secrets().createOrReplace(sb.build());

        final ActiveMQArtemis broker = new ActiveMQArtemis();
        broker.getMetadata().setName(name());

        // see https://access.redhat.com/documentation/en-us/red_hat_amq/2020
        //.q4/html/deploying_amq_broker_on_openshift/deploying-broker-on-ocp-using-operator_broker-ocp#operator-based-broker-deployment
        //-examples_broker-ocp
        final DeploymentPlan dp = new DeploymentPlan();
        dp.setSize(1);
        dp.setImage("placeholder");
        dp.setRequireLogin(getConfiguration().isRequireLogin());
        dp.setPersistenceEnabled(false);
        dp.setJournalType("nio");
        dp.setMessageMigration(true);

        broker.setSpec(new ActiveMQArtemisSpec());
        broker.getSpec().setDeploymentPlan(dp);

        broker.getSpec().setAdminUser(account().username());
        broker.getSpec().setAdminPassword(account().password());

        // exposed acceptor with ssl
        final Acceptors acceptor = new Acceptors();
        acceptor.setName("all-ssl");
        acceptor.setProtocols("all");
        acceptor.setPort(61636);
        acceptor.setExpose(true);
        acceptor.setSslEnabled(true);
        acceptor.setSslSecret(SSL_SECRET_NAME);

        //expose internal service
        final Acceptors internal = new Acceptors();
        internal.setName("all-internal");
        internal.setProtocols("all");
        internal.setPort(61626);
        internal.setExpose(false);
        internal.setSslEnabled(false);

        final List<Acceptors> acceptors = new ArrayList<>();
        acceptors.add(acceptor);
        acceptors.add(internal);

        broker.getSpec().setAcceptors(acceptors);

        return broker;
    }

    private Path materializeTrustStore() throws IOException {
        final Path ts = Paths.get("target", "tnb-trust-store" + new Date().getTime() + ".ts");
        try (InputStream is = this.getClass().getResourceAsStream("/client.ts")) {
            Files.copy(is, ts, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create truststore", e);
        }
        return ts;
    }

    private String encode(byte[] content) {
        return Base64.getEncoder().encodeToString(content);
    }

    private String encodeResource(String resource) {
        try {
            return encode(IOUtils.toByteArray(this.getClass().getResource("/" + resource)));
        } catch (IOException e) {
            throw new RuntimeException("Unable to encode resource " + resource, e);
        }
    }

    /**
     * Lazy initialized amq broker CLI. This way it prevents issue with creating unnecessary TNB namespace
     *
     * @return
     */
    private NonNamespaceOperation<ActiveMQArtemis, ActiveMQArtemisList, Resource<ActiveMQArtemis>> amqBrokerClient() {
        return OpenshiftClient.get().resources(ActiveMQArtemis.class, ActiveMQArtemisList.class).inNamespace(OpenshiftClient.get().getNamespace());
    }

    @Override
    public String operatorChannel() {
        return OPERATOR_CHANNEL;
    }

    @Override
    public String operatorName() {
        return OPERATOR_NAME;
    }

    @Override
    public String name() {
        return "tnb-amq-broker";
    }
}
