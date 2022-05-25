package software.tnb.kafka.resource.managed;

import software.tnb.kafka.configuration.RhoasConfiguration;
import software.tnb.kafka.service.Kafka;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

@AutoService(ManagedKafka.class)
public class ManagedKafka extends Kafka {
    @Override
    public String bootstrapServers() {
        return RhoasConfiguration.kafkaBootstrapServers();
    }

    @Override
    public String bootstrapSSLServers() {
        return bootstrapServers(); // keep the same bootstrap servers
    }

    @Override
    public void createTopic(String name, int partitions, int replicas) {
        // no-op, needs to be created manually beforehand
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        // no-op
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        // no-op, needs to be created manually beforehand
    }
}
