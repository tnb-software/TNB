package software.tnb.product.customizer;

import software.tnb.product.csb.customizer.SpringBootCustomizer;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;
import software.tnb.product.quarkus.camel.customizer.CamelQuarkusCustomizer;

import java.util.function.Consumer;

public enum Customizers implements Customizable {
    SPRINGBOOT {
        @Override
        public Customizer customize(Consumer<AbstractIntegrationBuilder<?>> i) {
            return new SpringBootCustomizer() {
                public void customize() {
                    i.accept(this.getIntegrationBuilder());
                }
            };
        }
    },

    QUARKUS {
        @Override
        public Customizer customize(Consumer<AbstractIntegrationBuilder<?>> i) {
            return new CamelQuarkusCustomizer() {
                public void customize() {
                    i.accept(this.getIntegrationBuilder());
                }
            };
        }
    }
}
