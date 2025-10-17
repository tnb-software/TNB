package software.tnb.product.util.jparser;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithName;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class AnnotationUtils {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotationUtils.class);

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
        final Optional<TypeDeclaration<?>> routeBuilder = compilationUnit.getTypes().stream()
            .filter(type -> type.asClassOrInterfaceDeclaration().getExtendedTypes()
                .stream()
                .anyMatch(extendedType -> "RouteBuilder".equals(extendedType.getNameAsString()))
            )
            .findAny();

        if (routeBuilder.isPresent()) {
            final TypeDeclaration<?> routeBuilderType = routeBuilder.get();
            final Set<String> routeBuilderAnnotations = routeBuilderType.getAnnotations().stream()
                .map(NodeWithName::getNameAsString).collect(Collectors.toSet());

            for (String annotation : annotations) {
                // if ever we would need to add repeatable annotation, this if block should be skipped and the method signature should be changed
                if (!routeBuilderAnnotations.contains(annotation)) {
                    addImports(compilationUnit, imports);
                    if (annotation.contains("(")) {
                        routeBuilderType.addSingleMemberAnnotation(StringUtils.substringBefore(annotation, "(")
                            , StringUtils.substringBetween(annotation, "(", ")"));
                    } else {
                        routeBuilderType.addAnnotation(annotation);
                    }
                } else {
                    LOG.trace("RouteBuilder class already contains {} annotation, not adding another one", annotation);
                }
            }
        }
    }

    private static void addImports(CompilationUnit compilationUnit, List<String> imports) {
        final Set<String> routeBuilderImports = compilationUnit.getImports().stream().map(NodeWithName::getNameAsString).collect(Collectors.toSet());
        for (String anImport : imports) {
            if (!routeBuilderImports.contains(anImport)) {
                compilationUnit.addImport(anImport);
            }
        }
    }
}
