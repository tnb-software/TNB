package org.jboss.fuse.tnb.product.customizer;

import org.jboss.fuse.tnb.product.integration.builder.AbstractIntegrationBuilder;

import java.util.function.Consumer;

public interface Customizable {
    Customizer customize(Consumer<AbstractIntegrationBuilder<?>> i);
}
