package software.tnb.product.customizer;

import software.tnb.product.ck.customizer.CamelKCustomizer;
import software.tnb.product.cq.customizer.QuarkusCustomizer;
import software.tnb.product.csb.customizer.SpringBootCustomizer;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

import java.util.function.Consumer;

public enum Customizers implements Customizable {
    CAMELK {
        @Override
        public Customizer customize(Consumer<AbstractIntegrationBuilder<?>> i) {
            return new CamelKCustomizer() {
                public void customize() {
                    i.accept(this.getIntegrationBuilder());
                }
            };
        }
    },
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
