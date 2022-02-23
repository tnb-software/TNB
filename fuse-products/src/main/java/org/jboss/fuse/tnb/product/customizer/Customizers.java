package org.jboss.fuse.tnb.product.customizer;

import org.jboss.fuse.tnb.product.ck.customizer.CamelKCustomizer;
import org.jboss.fuse.tnb.product.cq.customizer.QuarkusCustomizer;
import org.jboss.fuse.tnb.product.csb.customizer.SpringbootCustomizer;
import org.jboss.fuse.tnb.product.integration.builder.AbstractIntegrationBuilder;

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
            return new SpringbootCustomizer() {
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
