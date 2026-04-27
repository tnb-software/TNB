package software.tnb.product.quarkus.camel.customizer;

import software.tnb.product.util.jparser.AnnotationUtils;

import java.util.List;

/**
 * For camel quarkus we add @ApplicationScoped to RouteBuilder class
 */
public class ApplicationScopeCustomizer extends CamelQuarkusCustomizer {
    @Override
    public void customize() {
        if (!getIntegrationBuilder().isJBang()) {
            getIntegrationBuilder().getRouteBuilders().forEach(rb ->
                AnnotationUtils.addAnnotationsToRouteBuilder(rb, List.of("jakarta.enterprise.context.ApplicationScoped"),
                    List.of("ApplicationScoped"))
            );
        }
    }
}
