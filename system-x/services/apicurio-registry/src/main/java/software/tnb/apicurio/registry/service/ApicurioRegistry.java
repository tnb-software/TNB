package software.tnb.apicurio.registry.service;

import software.tnb.apicurio.registry.validation.ApicurioRegistryValidation;
import software.tnb.common.account.NoAccount;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import io.apicurio.registry.rest.client.RegistryClient;
import io.apicurio.registry.rest.client.RegistryClientFactory;

public abstract class ApicurioRegistry extends Service<NoAccount, RegistryClient, ApicurioRegistryValidation> implements WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(ApicurioRegistry.class);

    public abstract String url();

    protected abstract String clientUrl();

    public String defaultImage() {
        return "quay.io/fuse_qe/apicurio-registry-mem:2.4.3.Final";
    }

    public void openResources() {
        LOG.debug("Creating new Apicurio Registry client");
        client = RegistryClientFactory.create(clientUrl());
        validation = new ApicurioRegistryValidation(client);
    }

    public void closeResources() {
        validation = null;
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                LOG.warn("Unable to close Apicurio Registry client", e);
            }
        }
    }
}
