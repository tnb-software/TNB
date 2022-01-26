package org.jboss.fuse.tnb.product.csb.customizer;

import org.jboss.fuse.tnb.product.integration.Customizer;
import org.jboss.fuse.tnb.product.util.jparser.AnnotationUtils;

import java.util.List;

/**
 * For camel spring boot we add @Component to RouteBuilder class
 */
public class ComponentCustomizer extends Customizer {

    @Override
    public void customize() {
        AnnotationUtils.addAnnotationsToRouteBuilder(getIntegrationBuilder().getRouteBuilder(),
            List.of("org.springframework.stereotype.Component"),
            List.of("Component"));
    }
}
