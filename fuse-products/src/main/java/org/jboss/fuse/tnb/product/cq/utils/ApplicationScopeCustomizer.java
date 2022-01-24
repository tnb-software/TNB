package org.jboss.fuse.tnb.product.cq.utils;

import org.jboss.fuse.tnb.product.integration.Customizer;
import org.jboss.fuse.tnb.product.util.jparser.AnnotationUtils;

import java.util.List;

/**
 * For camel quarkus we add @ApplicationScoped to RouteBuilder class
 */
public class ApplicationScopeCustomizer extends Customizer {
    @Override
    public void customize() {
        AnnotationUtils.addAnnotationsToRouteBuilder(getIntegrationBuilder().getRouteBuilder(),
            List.of("javax.enterprise.context.ApplicationScoped"),
            List.of("ApplicationScoped"));
    }
}
