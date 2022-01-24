package org.jboss.fuse.tnb.product.csb.customizer;

import org.jboss.fuse.tnb.product.integration.Customizer;

import com.github.javaparser.ast.CompilationUnit;

/**
 * For camel spring boot we add @Component to RouteBuilder class
 */
public class ComponentCustomizer extends Customizer {

    @Override
    public void customize() {
        CompilationUnit routeBuilderCU = getIntegrationBuilder().getRouteBuilder();

        routeBuilderCU.addImport("org.springframework.stereotype.Component");

        // Add Component annotation to the class that extends RouteBuilder
        routeBuilderCU.getTypes().stream()
            .filter(type -> type.asClassOrInterfaceDeclaration().getExtendedTypes()
                .stream()
                .anyMatch(extendedType -> "RouteBuilder".equals(extendedType.getNameAsString()))
            )
            .findAny()
            .ifPresent(routeBuilderType -> routeBuilderType.addAnnotation("Component"));
    }
}
