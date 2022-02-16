package org.jboss.fuse.tnb.product.ck.customizer;

import io.fabric8.camelk.v1.IntegrationSpecBuilder;

/**
 * Allows customizing Spec inside Integration custom resource.
 */
public interface IntegrationSpecCustomizer {

    void customizeIntegration(IntegrationSpecBuilder integrationSpecBuilder);
}
