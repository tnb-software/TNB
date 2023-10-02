package software.tnb.product.ck.customizer;

import org.apache.camel.v1.IntegrationSpec;

/**
 * Allows customizing Spec inside Integration custom resource.
 */
public interface IntegrationSpecCustomizer {

    void customizeIntegration(IntegrationSpec integrationSpec);
}
