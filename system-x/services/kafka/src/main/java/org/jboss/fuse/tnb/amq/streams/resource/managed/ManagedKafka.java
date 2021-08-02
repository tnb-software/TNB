package org.jboss.fuse.tnb.amq.streams.resource.managed;

import org.jboss.fuse.tnb.amq.streams.account.ManagedKafkaAccount;
import org.jboss.fuse.tnb.amq.streams.service.Kafka;
import org.jboss.fuse.tnb.common.account.Accounts;
import org.jboss.fuse.tnb.common.config.RhoasConfiguration;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

@AutoService(ManagedKafka.class)
public class ManagedKafka extends Kafka {
    private ManagedKafkaAccount account;

    @Override
    public String bootstrapServers(boolean tls) {
        return RhoasConfiguration.kafkaBootstrapServers();
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

    public ManagedKafkaAccount account() {
        if (account == null) {
            account = Accounts.get(ManagedKafkaAccount.class);
        }
        return account;
    }

}
