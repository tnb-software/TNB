package software.tnb.product.customizer;

import software.tnb.product.cq.customizer.QuarkusCustomizer;
import software.tnb.product.csb.customizer.SpringBootCustomizer;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

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
            return new QuarkusCustomizer() {
                public void customize() {
                    i.accept(this.getIntegrationBuilder());
                }
            };
        }
    }
}
