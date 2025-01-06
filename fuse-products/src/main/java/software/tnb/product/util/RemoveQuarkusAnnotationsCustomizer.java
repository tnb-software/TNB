package software.tnb.product.util;

import software.tnb.product.customizer.ProductsCustomizer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

import java.util.ArrayList;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

public class RemoveQuarkusAnnotationsCustomizer extends ProductsCustomizer {
    private void removeAnnotation() {
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        getIntegrationBuilder().getRouteBuilder().ifPresent(compilationUnits::add);
        compilationUnits.addAll(getIntegrationBuilder().getAdditionalClasses());
        for (CompilationUnit cu : compilationUnits) {
            cu.accept(new ModifierVisitor<>() {
                @Override
                public Visitable visit(ClassOrInterfaceDeclaration n, Object arg) {
                    super.visit(n, arg);
                    // Remove annotation if present
                    n.getAnnotations().removeIf(a -> RegisterForReflection.class.getSimpleName().equals(a.getName().getIdentifier()));
                    return n;
                }
            }, null);
            // Remove import if present
            cu.getImports().removeIf(i -> i.getName().getIdentifier().contains(RegisterForReflection.class.getSimpleName()));
        }
    }

    @Override
    public void customizeQuarkus() {
        // no-op
    }

    @Override
    public void customizeSpringboot() {
        removeAnnotation();
    }
}
