package software.tnb.product.ck.utils;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.WaitUtils;

import org.apache.camel.v1.Integration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.OwnerReferenceBuilder;

/**
 * Runnable that sets the owner reference of a given openshift resource to the integration object.
 */
public class OwnerReferenceSetter implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(OwnerReferenceSetter.class);
    private final String integrationNameSubstring;
    private final HasMetadata target;

    public OwnerReferenceSetter(HasMetadata target, String integrationNameSubstring) {
        this.target = target;
        this.integrationNameSubstring = integrationNameSubstring;
    }

    @Override
    public void run() {
        Integration i = null;
        int maxRetries = 30;
        int retries = 0;
        while (i == null) {
            WaitUtils.sleep(1000);
            i = OpenshiftClient.get().resources(Integration.class).list().getItems().stream()
                .filter(integration -> integration.getMetadata().getName().contains(integrationNameSubstring)).findFirst().orElse(null);
            retries++;
            if (retries > maxRetries) {
                LOG.warn(
                    "Unable to find Integration object with name containing substring {} within 30 seconds, not setting owner reference to {} {}",
                    integrationNameSubstring, target.getKind(), target.getMetadata().getName());
                return;
            }
        }

        ObjectMeta metadata = target.getMetadata();
        metadata.setOwnerReferences(Collections.singletonList(new OwnerReferenceBuilder()
            .withApiVersion(i.getApiVersion())
            .withKind(i.getKind())
            .withName(i.getMetadata().getName())
            .withUid(i.getMetadata().getUid())
            .build())
        );
        target.setMetadata(metadata);
       
        LOG.debug("Setting integration {} as the owner of {} {}", i.getMetadata().getName(), target.getKind(), target.getMetadata().getName());
        try {
            OpenshiftClient.get().resource(target).createOrReplace();
        } catch (Exception e) {
            LOG.warn("Unable to update object to set owner reference: ", e);
        }
    }
}
