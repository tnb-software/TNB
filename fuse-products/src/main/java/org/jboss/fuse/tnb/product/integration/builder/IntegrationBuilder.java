package org.jboss.fuse.tnb.product.integration.builder;

/**
 * Implementation of AbstractIntegrationBuilder.
 */
public final class IntegrationBuilder extends AbstractIntegrationBuilder<IntegrationBuilder> {

    public IntegrationBuilder(String name) {
        super(name);
    }

    @Override
    protected IntegrationBuilder self() {
        return this;
    }
}
