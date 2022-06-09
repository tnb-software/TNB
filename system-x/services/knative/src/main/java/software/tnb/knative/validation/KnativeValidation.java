package software.tnb.knative.validation;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.eventing.v1.Broker;
import io.fabric8.knative.eventing.v1.BrokerBuilder;
import io.fabric8.knative.messaging.v1.Channel;
import io.fabric8.knative.messaging.v1.ChannelBuilder;
import io.fabric8.knative.messaging.v1.InMemoryChannel;
import io.fabric8.kubernetes.api.model.HasMetadata;

public class KnativeValidation {
    private static final Logger LOG = LoggerFactory.getLogger(KnativeValidation.class);
    // This string is from the error message when you try to create a resource with invalid name
    private static final String NAME_VALIDATION_REGEX = "[a-z0-9]([-a-z0-9]*[a-z0-9])?(\\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*";

    private final KnativeClient client;

    private final List<HasMetadata> createdItems;

    public KnativeValidation(KnativeClient client) {
        this.client = client;
        this.createdItems = new ArrayList<>();
    }

    public InMemoryChannel createInMemoryChannel(String name) {
        validateName(name);
        LOG.debug("Creating In-Memory Channel {}", name);
        final Channel channel = new ChannelBuilder()
            .withNewMetadata()
            .withName(name)
            .endMetadata()
            .withNewSpec()
            .withNewChannelTemplate()
            .withApiVersion("messaging.knative.dev/v1")
            .withKind("InMemoryChannel")//other type: e.g. KafkaChannel https://knative.dev/docs/eventing/channels/create-default-channel/)
            .endChannelTemplate()
            .endSpec()
            .build();
        client.channels().createOrReplace(channel);
        WaitUtils.waitFor(() -> client.inMemoryChannels().withName(name).get() != null, 50, 5000L,
            String.format("Waiting until the InmemoryChannel %s is available", name));
        createdItems.add(channel);
        return client.inMemoryChannels().withName(name).get();
    }

    public Broker createBroker(String name) {
        validateName(name);
        LOG.debug("Creating Broker {}", name);
        final Broker broker = new BrokerBuilder()
            .withNewMetadata()
            .withName(name)
            .endMetadata()
            .build();
        client.brokers().createOrReplace(broker);
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

    public String getRouteUrl(String serviceName) {
        return client.routes().withName(serviceName).get().getStatus().getUrl();
    }

    private void validateName(String name) {
        if (!name.matches(NAME_VALIDATION_REGEX)) {
            throw new IllegalArgumentException("Name must consist of lower case alphanumeric characters, '-' or '.',"
                + " and must start and end with an alphanumeric character (was: " + name + ")");
        }
    }
}
