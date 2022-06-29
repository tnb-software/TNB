package software.tnb.jms.amq.service.openshift;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import software.tnb.common.deployment.OpenshiftDeployable;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithInClusterHostname;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.jms.amq.service.AMQBroker;
import software.tnb.jms.amq.service.openshift.generated.Acceptor;
import software.tnb.jms.amq.service.openshift.generated.ActiveMQArtemis;
import software.tnb.jms.amq.service.openshift.generated.ActiveMQArtemisList;
import software.tnb.jms.amq.service.openshift.generated.ActiveMQArtemisSpec;
import software.tnb.jms.amq.service.openshift.generated.DeploymentPlan;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import javax.jms.Connection;
import javax.jms.JMSException;

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

import cz.xtf.core.openshift.OpenShiftWaiters;
import cz.xtf.core.openshift.helpers.ResourceFunctions;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.openshift.api.model.Route;

@AutoService(AMQBroker.class)
public class OpenshiftAMQBroker extends AMQBroker implements OpenshiftDeployable, WithInClusterHostname, WithExternalHostname {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftAMQBroker.class);

    public static final String BROKER_NAME = "tnb-amq-broker";
    private static final String SSL_SECRET_NAME = "tnb-ssl-secret";

    private static final String CHANNEL = "7.x";
    private static final String OPERATOR_NAME = "amq-broker-rhel8";
    private static final String SOURCE = "redhat-operators";
    private static final String SUBSCRIPTION_NAME = "tnb-amq-broker";
    private static final String SUBSCRIPTION_NAMESPACE = "openshift-marketplace";

    private static final CustomResourceDefinitionContext ARTEMIS_CTX = new CustomResourceDefinitionContext.Builder()
        .withName("ActiveMQArtemis")
        .withGroup("broker.amq.io")
        .withVersion("v2alpha5")
        .withPlural("activemqartemises")
        .withScope("Namespaced")
        .build();

    @Override
    public void create() {
        if (isReady()) {
            LOG.debug("Amq broker operator already installed");
        } else {
            if (OpenshiftClient.get().getLabeledPods("name", "amq-broker-operator").isEmpty()) {
                LOG.debug("Creating Amq broker operator");
                // Create subscription for amq broker operator
                OpenshiftClient.get()
                    .createSubscription(CHANNEL, OPERATOR_NAME, SOURCE, SUBSCRIPTION_NAME, SUBSCRIPTION_NAMESPACE,
                        OpenshiftClient.get().getNamespace(),
                        false);
                OpenshiftClient.get().waitForInstallPlanToComplete(SUBSCRIPTION_NAME);
            } else {
                LOG.debug("Amq broker operator pod is already present");
            }
            // Create amq-broker custom resource
            amqBrokerCli().createOrReplace(createBrokerCR());
        }
    }

    @Override
    public boolean isReady() {
        return ResourceFunctions.areExactlyNPodsReady(1).apply(OpenshiftClient.get().getLabeledPods("ActiveMQArtemis", BROKER_NAME));
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().operatorHub().subscriptions().withName(SUBSCRIPTION_NAME).get() != null
            && !amqBrokerCli().list().getItems().isEmpty();
    }

    @Override
    public String inClusterHostname() {
        final String podName = OpenshiftClient.get().getLabeledPods("ActiveMQArtemis", BROKER_NAME).get(0).getMetadata().getName();
        return String.format("%s.%s-hdls-svc.%s.svc.cluster.local", podName, BROKER_NAME, OpenshiftClient.get().getNamespace());
    }

    @Override
    public String externalHostname() {
        final List<Route>
            routes = OpenshiftClient.get().routes().withLabel("ActiveMQArtemis", BROKER_NAME).list()
            .getItems();

        if (routes.size() != 1) {
            throw new RuntimeException("Expected single route to be present but was " + routes.size());
        }

        return routes.get(0).getSpec().getHost();
    }

    @Override
    public void undeploy() {
        amqBrokerCli().withName(BROKER_NAME).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false).areExactlyNPodsRunning(0, "ActiveMQArtemis", BROKER_NAME)
            .timeout(120_000).waitFor();
        OpenshiftClient.get().deleteSecret(SSL_SECRET_NAME);
        OpenshiftClient.get().deleteSubscription(SUBSCRIPTION_NAME);
        OpenShiftWaiters.get(OpenshiftClient.get(), () -> false).areExactlyNPodsRunning(0, "name", "amq-broker-operator")
            .timeout(120_000).waitFor();
    }

    @Override
    public void openResources() {
        connection = createConnection();
    }

    @Override
    public void closeResources() {
        try {
            connection.close();
        } catch (JMSException e) {
            throw new RuntimeException("Can't close JMS connection");
        }
    }

    @Override
    //return in-cluster broker URL
    public String brokerUrl() {
        return inClusterHostname();
    }

    @Override
    protected String mqttUrl() {
        return String.format("ssl://%s:443", externalHostname());
    }

    @Override
    public int getPortMapping(int port) {
        //redirect all to the internal acceptor
        return 61626;
    }

    // create connection for external client
    private Connection createConnection() {
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
            "client.ts", encodeResource("broker.ts"),
            "broker.ks", encodeResource("broker.ks")
        );

        SecretBuilder sb = new SecretBuilder().editOrNewMetadata().withName(SSL_SECRET_NAME).endMetadata().withData(data);
        OpenshiftClient.get().secrets().createOrReplace(sb.build());

        final ActiveMQArtemis broker = new ActiveMQArtemis();
        broker.getMetadata().setName(BROKER_NAME);

        // see https://access.redhat.com/documentation/en-us/red_hat_amq/2020
        //.q4/html/deploying_amq_broker_on_openshift/deploying-broker-on-ocp-using-operator_broker-ocp#operator-based-broker-deployment
        //-examples_broker-ocp
        final DeploymentPlan dp = new DeploymentPlan();
        dp.setSize(1);
        dp.setImage("placeholder");
        dp.setRequireLogin(false);
        dp.setPersistenceEnabled(false);
        dp.setJournalType("nio");
        dp.setMessageMigration(true);

        broker.setSpec(new ActiveMQArtemisSpec());
        broker.getSpec().setDeploymentPlan(dp);

        broker.getSpec().setAdminUser(account().username());
        broker.getSpec().setAdminPassword(account().password());

        // exposed acceptor with ssl
        final Acceptor acceptor = new Acceptor();
        acceptor.setName("all-ssl");
        acceptor.setProtocols("all");
        acceptor.setPort(61636);
        acceptor.setExpose(true);
        acceptor.setSslEnabled(true);
        acceptor.setSslSecret(SSL_SECRET_NAME);

        //expose internal service
        final Acceptor internal = new Acceptor();
        internal.setName("all-internal");
        internal.setProtocols("all");
        internal.setPort(61626);
        internal.setExpose(false);
        internal.setSslEnabled(false);

        final List<Acceptor> acceptors = new ArrayList<>();
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
    private NonNamespaceOperation<ActiveMQArtemis, ActiveMQArtemisList, Resource<ActiveMQArtemis>> amqBrokerCli() {
        return OpenshiftClient.get().customResources(ARTEMIS_CTX, ActiveMQArtemis.class, ActiveMQArtemisList.class)
            .inNamespace(OpenshiftClient.get().getNamespace());
    }
}
