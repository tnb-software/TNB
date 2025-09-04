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
        if (!getIntegrationBuilder().isJBang()) {
            getIntegrationBuilder().getRouteBuilder().ifPresent(rb ->
                AnnotationUtils.addAnnotationsToRouteBuilder(rb, List.of("jakarta.enterprise.context.ApplicationScoped"),
                    List.of("ApplicationScoped"))
            );
        }
    }
}
