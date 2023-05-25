package software.tnb.product.util.jparser;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.ast.CompilationUnit;

import java.util.List;

public final class AnnotationUtils {

    private AnnotationUtils() { }

    public static void addAnnotationsToClass(CompilationUnit compilationUnit, List<String> imports, List<String> annotations) {
        addImports(compilationUnit, imports);

        compilationUnit.getTypes().stream()
            .findAny()
            .ifPresent(routeBuilderType -> {
                for (String annotation : annotations) {
                    routeBuilderType.addAnnotation(annotation);
                }
            });
    }

    public static void addAnnotationsToRouteBuilder(CompilationUnit compilationUnit, List<String> imports, List<String> annotations) {
        addImports(compilationUnit, imports);

        compilationUnit.getTypes().stream()
            .filter(type -> type.asClassOrInterfaceDeclaration().getExtendedTypes()
                .stream()
                .anyMatch(extendedType -> "RouteBuilder".equals(extendedType.getNameAsString()))
            )
            .findAny()
            .ifPresent(routeBuilderType -> {
                for (String annotation : annotations) {
                    if (annotation.contains("(")) {
                        routeBuilderType.addSingleMemberAnnotation(StringUtils.substringBefore(annotation, "(")
                            , StringUtils.substringBetween(annotation, "(", ")"));
                    } else {
                        routeBuilderType.addAnnotation(annotation);
                    }
                }
            });
    }

    private static void addImports(CompilationUnit compilationUnit, List<String> imports) {
        for (String anImport : imports) {
            compilationUnit.addImport(anImport);
        }
    }
}
