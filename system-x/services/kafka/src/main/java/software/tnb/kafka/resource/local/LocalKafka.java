package software.tnb.kafka.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.kafka.service.Kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(Kafka.class)
public class LocalKafka extends Kafka implements ContainerDeployable<StrimziContainer>, WithDockerImage {

    private static final Logger LOG = LoggerFactory.getLogger(LocalKafka.class);
    private StrimziContainer strimziContainer;

    @Override
    public String bootstrapServers() {
        return strimziContainer.getHost() + ":" + strimziContainer.getKafkaPort();
    }

    @Override
    public String bootstrapSSLServers() {
        return bootstrapServers(); //always plain for local kafka
    }

    @Override
    public void createTopic(String name, int partitions, int replicas) {
        // no-op
    }

    @Override
    public StrimziContainer container() {
        return strimziContainer;
    }

    @Override
    public void deploy() {
        LOG.info("Starting Kafka container (KRaft mode)");
        strimziContainer = new StrimziContainer(image());
        strimziContainer.start();
        LOG.info("Kafka container started");
    }

    @Override
    public void undeploy() {
        if (strimziContainer != null) {
            LOG.info("Stopping Kafka container");
            strimziContainer.stop();
        }
    }

    @Override
    public void openResources() {
        props.setProperty("bootstrap.servers", bootstrapServers());
        super.openResources();
    }

    public String defaultImage() {
        return "registry.redhat.io/amq-streams/kafka-41-rhel9:3.2.0-13";
    }
}
