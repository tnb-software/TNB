package org.jboss.fuse.tnb.product.customizer;

import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;

import java.util.function.Consumer;

public interface Customizable {
    Customizer customize(Consumer<IntegrationBuilder> i);
}
