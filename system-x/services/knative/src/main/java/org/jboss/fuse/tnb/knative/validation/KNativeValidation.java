package org.jboss.fuse.tnb.knative.validation;

import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.eventing.v1.Broker;
import io.fabric8.knative.eventing.v1.BrokerBuilder;
import io.fabric8.knative.messaging.v1.InMemoryChannel;
import io.fabric8.knative.messaging.v1.InMemoryChannelBuilder;
import io.fabric8.kubernetes.api.model.HasMetadata;

public class KNativeValidation {
    private static final Logger LOG = LoggerFactory.getLogger(KNativeValidation.class);

    private final KnativeClient client;

    private final List<HasMetadata> createdItems;

    public KNativeValidation(KnativeClient client) {
        this.client = client;
        this.createdItems = new ArrayList<>();
    }

    public InMemoryChannel createInMemoryChannel(String name) {
        LOG.debug("Creating In-Memory Channel {}", name);
        final InMemoryChannel channel = new InMemoryChannelBuilder()
            .withNewMetadata()
            .withName(name)
            .endMetadata()
            .build();
        client.inMemoryChannels().create(channel);
        createdItems.add(channel);
        return channel;
    }

    public Broker createBroker(String name) {
        LOG.debug("Creating Broker {}", name);
        final Broker broker = new BrokerBuilder()
            .withNewMetadata()
            .withName(name)
            .endMetadata()
            .build();
        client.brokers().create(broker);
        createdItems.add(broker);
        return broker;
    }

    public void deleteCreatedResources() {
        Collections.reverse(createdItems);
        for (HasMetadata createdItem : createdItems) {
            LOG.debug("Deleting {} {}", createdItem.getKind(), createdItem.getMetadata().getName());
            OpenshiftClient.get().resource(createdItem).delete();
        }
        createdItems.clear();
    }
}
