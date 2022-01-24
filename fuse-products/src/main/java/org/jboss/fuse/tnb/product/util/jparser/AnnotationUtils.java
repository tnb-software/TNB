package org.jboss.fuse.tnb.product.util.jparser;

import com.github.javaparser.ast.CompilationUnit;

import java.util.List;

public final class AnnotationUtils {

    private AnnotationUtils() { }

    public static void addAnnotationsToRouteBuilder(CompilationUnit compilationUnit, List<String> imports, List<String> annotations) {
        for (String anImport : imports) {
            compilationUnit.addImport(anImport);
        }

        compilationUnit.getTypes().stream()
            .filter(type -> type.asClassOrInterfaceDeclaration().getExtendedTypes()
                .stream()
                .anyMatch(extendedType -> "RouteBuilder".equals(extendedType.getNameAsString()))
            )
            .findAny()
            .ifPresent(routeBuilderType -> {
                for (String annotation : annotations) {
                    routeBuilderType.addAnnotation(annotation);
                }
            });
    }
}
