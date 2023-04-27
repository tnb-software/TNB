package software.tnb.kafka.resource.openshift;

import static org.awaitility.Awaitility.await;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.deployment.ReusableOpenshiftDeployable;
import software.tnb.common.deployment.WithName;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.kafka.service.Kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.strimzi.api.kafka.KafkaList;
import io.strimzi.api.kafka.KafkaTopicList;
import io.strimzi.api.kafka.model.KafkaBuilder;
import io.strimzi.api.kafka.model.KafkaTopic;
import io.strimzi.api.kafka.model.KafkaTopicBuilder;
import io.strimzi.api.kafka.model.listener.arraylistener.KafkaListenerType;
import io.strimzi.api.kafka.model.status.Condition;

@AutoService(Kafka.class)
public class OpenshiftKafka extends Kafka implements ReusableOpenshiftDeployable, WithName, WithOperatorHub {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftKafka.class);

    private static final String CRD_GROUP = "kafka.strimzi.io";
    private static final String CRD_VERSION = "v1beta2";

    private static final CustomResourceDefinitionContext KAFKA_CONTEXT = new CustomResourceDefinitionContext.Builder()
        .withName("Kafka")
        .withGroup(CRD_GROUP)
        .withVersion(CRD_VERSION)
        .withPlural("kafkas")
        .withScope("Namespaced")
        .build();

    private static final MixedOperation<io.strimzi.api.kafka.model.Kafka, KafkaList, Resource<io.strimzi.api.kafka.model.Kafka>> KAFKA_CRD_CLIENT =
        OpenshiftClient.get().customResources(KAFKA_CONTEXT, io.strimzi.api.kafka.model.Kafka.class, KafkaList.class);

    private static final CustomResourceDefinitionContext KAFKA_TOPIC_CONTEXT = new CustomResourceDefinitionContext.Builder()
        .withName("KafkaTopic")
        .withGroup(CRD_GROUP)
        .withVersion(CRD_VERSION)
        .withPlural("kafkatopics")
        .withScope("Namespaced")
        .build();

    private static final MixedOperation<KafkaTopic, KafkaTopicList, Resource<KafkaTopic>> KAFKA_TOPIC_CRD_CLIENT =
        OpenshiftClient.get().customResources(KAFKA_TOPIC_CONTEXT, KafkaTopic.class, KafkaTopicList.class);

    @Override
    public long waitTime() {
        return 600_000;
    }

    @Override
    public Predicate<Pod> podSelector() {
        return p -> OpenshiftClient.get().hasLabels(p, Map.of("strimzi.io/name", name() + "-kafka"));
    }

    @Override
    public void undeploy() {
        // https://github.com/strimzi/strimzi-kafka-operator/issues/5042
        if (!TestConfiguration.skipTearDownOpenshiftAMQStreams()) {
            KAFKA_CRD_CLIENT.withName(name()).withPropagationPolicy(DeletionPropagation.BACKGROUND).delete();
            // FIXME verify
            await()
                .atMost(2, TimeUnit.MINUTES)
                .until(() -> OpenshiftClient.get().getLabeledPods("strimzi.io/cluster", name()), List::isEmpty);
            deleteSubscription(() -> OpenshiftClient.get().getLabeledPods("strimzi.io/kind", "cluster-operator").isEmpty());
        }
    }

    @Override
    public void openResources() {
        createBasicUser();
        extractCertificate();
        connectionProperties();
        super.openResources();
    }

    @Override
    public void create() {
        createSubscription();
        deployKafkaCR();
    }

    /**
     * https://strimzi.io/blog/2023/01/25/running-apache-kafka-on-fips-enabled-kubernetes-cluster/
     * until Strimzi 0.33 will be used
     *
     * @return
     */
    @Override
    public List<EnvVar> getOperatorEnvVariables() {
        return List.of(new EnvVar("FIPS_MODE", "disabled", null));
    }

    @Override
    public boolean isReady() {
        try {
            return KAFKA_CRD_CLIENT
                .withName(name())
                .get()
                .getStatus().getConditions()
                .stream()
                .filter(c -> "Ready".equals(c.getType()))
                .map(Condition::getStatus)
                .map(Boolean::parseBoolean)
                .findFirst().orElse(false);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean isDeployed() {
        return OpenshiftClient.get().getLabeledPods("name", "amq-streams-cluster-operator").size() != 0
            && KAFKA_CRD_CLIENT.withName(name()).get() != null;
    }

    @Override
    public String name() {
        return "my-kafka-cluster";
    }

    private void deployKafkaCR() {
        //@formatter:off
        io.strimzi.api.kafka.model.Kafka kafka = new KafkaBuilder()
            .withNewMetadata()
                .withName(name())
            .endMetadata()
            .withNewSpec()
                .withNewKafka()
                    .withReplicas(1)
                    .addNewListener()
                        .withName("plain")
                        .withPort(9092)
                        .withTls(false)
                        .withType(KafkaListenerType.INTERNAL)
                    .endListener()
                    .addNewListener()
                        .withName("route")
                        .withPort(9093)
                        .withTls(true)
                        .withType(KafkaListenerType.ROUTE)
                    .endListener()
                    .withNewEphemeralStorage()
                    .endEphemeralStorage()
                    .addToConfig("offsets.topic.replication.factor", 1)
                    .addToConfig("transaction.state.log.replication.factor", 1)
                    .addToConfig("transaction.state.log.min.isr", 1)
                .endKafka()
                .withNewZookeeper()
                    .withReplicas(1)
                    .withNewEphemeralStorage().endEphemeralStorage()
                .endZookeeper()
                .withNewEntityOperator()
                    .withNewTopicOperator().endTopicOperator()
                    .withNewUserOperator().endUserOperator()
                .endEntityOperator()
                .endSpec()
            .build();
        //@formatter:on

        KAFKA_CRD_CLIENT.createOrReplace(kafka);
    }

    @Override
    public String bootstrapServers() {
        return findBootstrapServers("plain");
    }

    @Override
    public String bootstrapSSLServers() {
        return findBootstrapServers("route");
    }

    private String findBootstrapServers(String listnerType) {
        return KAFKA_CRD_CLIENT.withName(name()).get().getStatus().getListeners()
            .stream()
            .filter(l -> listnerType.equals(l.getType()))
            .findFirst().get().getBootstrapServers();
    }

    @Override
    public void createTopic(String name, int partitions, int replicas) {
        //@formatter:off
        KafkaTopic kafkaTopic = new KafkaTopicBuilder()
            .withNewMetadata()
                .withName(name)
                .addToLabels("strimzi.io/cluster", name())
            .endMetadata()
            .withNewSpec()
                .withPartitions(partitions)
                .withReplicas(replicas)
            .endSpec()
            .build();
        //@formatter:on
        KAFKA_TOPIC_CRD_CLIENT.createOrReplace(kafkaTopic);
    }

    private void createBasicUser() { // via https://access.redhat.com/documentation/en-us/red_hat_amq/2021
        // .q2/html-single/using_amq_streams_on_openshift/index#type-KafkaClientAuthenticationPlain-reference
        String password = Base64.getEncoder().encodeToString(account().basicPassword().getBytes());
        Map<String, String> labels = new HashMap<>();
        labels.put("strimzi.io/kind", "KafkaUser");
        labels.put("strimzi.io/cluster", name());

        SecretBuilder sb = new SecretBuilder()
            .withApiVersion("v1")
            .editOrNewMetadata().withName(account().basicUser()).withLabels(labels).endMetadata()
            .withType("Opaque")
            .withData(Collections.singletonMap("password", password));
        OpenshiftClient.get().secrets().createOrReplace(sb.build());
    }

    @Override
    public void cleanup() {
        LOG.debug("Cleaning kafka instance");
        AdminClient adminClient = AdminClient.create(props);
        try {
            adminClient.deleteTopics(adminClient.listTopics().names().get());
            adminClient.close();
        } catch (Exception e) {
            LOG.warn("Unable to clean kafka instance", e);
        }
    }

    public void extractCertificate() {
        LOG.debug("Extracting kafka certificate");
        String cert = new String(Base64.getDecoder()
            .decode(OpenshiftClient.get().secrets().withName(name() + "-cluster-ca-cert").get().getData().get("ca.crt")));
        String password = new String(Base64.getDecoder()
            .decode(OpenshiftClient.get().secrets().withName(name() + "-cluster-ca-cert").get().getData().get("ca.password")));
        account().setTrustStorePassword(password);
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(null, password.toCharArray());
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            try (ByteArrayInputStream is = new ByteArrayInputStream(cert.getBytes())) {
                ks.setCertificateEntry("ca.crt", cf.generateCertificate(is));
            }

            FileOutputStream fos = new FileOutputStream(account().trustStore());
            ks.store(fos, password.toCharArray());
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException("Unable to extract kafka certificate", e);
        }
    }

    private void connectionProperties() {
        props.setProperty("bootstrap.servers", bootstrapSSLServers());
        props.setProperty("security.protocol", "SSL");
        props.setProperty("ssl.truststore.password", account().trustStorePassword());
        props.setProperty("ssl.truststore.location", new File(account().trustStore()).getAbsolutePath());
        props.setProperty("ssl.truststore.type", "PKCS12");
    }

    @Override
    public String operatorName() {
        return "amq-streams";
    }
}
