package org.jboss.fuse.tnb.product.cq.utils;

import org.jboss.fuse.tnb.product.integration.Customizer;

import com.github.javaparser.ast.CompilationUnit;

/**
 * For camel quarkus we add @ApplicationScoped to RouteBuilder class
 */
public class ApplicationScopeCustomizer extends Customizer {
    @Override
    public void customize() {
        CompilationUnit routeBuilderCU = getIntegrationBuilder().getRouteBuilder();

        routeBuilderCU.addImport("javax.enterprise.context.ApplicationScoped");

        // Add ApplicationScoped annotation to the class that extends RouteBuilder
        routeBuilderCU.getTypes().stream()
            .filter(type -> type.asClassOrInterfaceDeclaration().getExtendedTypes()
                .stream()
                .anyMatch(extendedType -> "RouteBuilder".equals(extendedType.getNameAsString()))
            )
            .findAny()
            .ifPresent(routeBuilderType -> routeBuilderType.addAnnotation("ApplicationScoped"));
    }
}
