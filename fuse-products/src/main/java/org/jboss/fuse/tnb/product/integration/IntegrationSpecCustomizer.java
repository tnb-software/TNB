package org.jboss.fuse.tnb.product.integration;

import io.fabric8.camelk.v1.IntegrationSpecBuilder;

/**
 * Abstract class for customizations for routebuilder / application properties.
 */
public interface IntegrationSpecCustomizer {

    void customizeIntegration(IntegrationSpecBuilder integrationSpecBuilder);
}
