package software.tnb.product.customizer;

import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

import java.util.function.Consumer;

public interface Customizable {
    Customizer customize(Consumer<AbstractIntegrationBuilder<?>> i);
}
