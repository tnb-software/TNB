package org.jboss.fuse.tnb.amq.streams.resource.managed;

import org.jboss.fuse.tnb.amq.streams.service.Kafka;
import org.jboss.fuse.tnb.amq.streams.validation.KafkaValidation;
import org.jboss.fuse.tnb.common.config.RhoasConfiguration;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

@AutoService(ManagedKafka.class)
public class ManagedKafka extends Kafka {
    @Override
    public String bootstrapServers() {
        return RhoasConfiguration.kafkaBootstrapServers();
    }

    @Override
    public void createTopic(String name, int partitions, int replicas) {
        // no-op, needs to be created manually beforehand
    }

    @Override
    public KafkaValidation validation() {
        // TODO(somebody): if you know something about managed kafka, finish this if it is needed
        return null;
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
