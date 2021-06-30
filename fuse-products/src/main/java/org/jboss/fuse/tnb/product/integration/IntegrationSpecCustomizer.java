package org.jboss.fuse.tnb.product.integration;

import org.jboss.fuse.tnb.product.ck.generated.IntegrationSpec;

/**
 * Abstract class for customizations for routebuilder / application properties.
 */
public interface IntegrationSpecCustomizer {

    void customizeIntegration(IntegrationSpec integrationSpec);
}
