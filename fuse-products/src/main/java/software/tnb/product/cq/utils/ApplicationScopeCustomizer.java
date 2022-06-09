package software.tnb.product.cq.utils;

import software.tnb.product.cq.customizer.QuarkusCustomizer;
import software.tnb.product.util.jparser.AnnotationUtils;

import java.util.List;

/**
 * For camel quarkus we add @ApplicationScoped to RouteBuilder class
 */
public class ApplicationScopeCustomizer extends QuarkusCustomizer {
    @Override
    public void customize() {
        getIntegrationBuilder().getRouteBuilder().ifPresent(rb ->
            AnnotationUtils.addAnnotationsToRouteBuilder(rb, List.of("javax.enterprise.context.ApplicationScoped"), List.of("ApplicationScoped"))
        );
    }
}
