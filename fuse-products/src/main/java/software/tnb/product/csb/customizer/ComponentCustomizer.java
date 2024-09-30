package software.tnb.product.csb.customizer;

import software.tnb.product.util.jparser.AnnotationUtils;

import java.util.List;

/**
 * For camel spring boot we add @Component to RouteBuilder class
 */
public class ComponentCustomizer extends SpringBootCustomizer {

    @Override
    public void customize() {
        // Camel export using JBang adds the annotation automatically
        if (!getIntegrationBuilder().isJBang()) {
            getIntegrationBuilder().getRouteBuilder().ifPresent(rb ->
                AnnotationUtils.addAnnotationsToRouteBuilder(rb, List.of("org.springframework.stereotype.Component"), List.of("Component")));
        }
    }
}
