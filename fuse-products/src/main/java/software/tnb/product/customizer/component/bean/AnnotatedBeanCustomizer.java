package software.tnb.product.customizer.component.bean;

import software.tnb.product.cq.configuration.QuarkusConfiguration;
import software.tnb.product.customizer.ProductsCustomizer;
import software.tnb.product.util.jparser.AnnotationUtils;

import com.github.javaparser.ast.CompilationUnit;

import java.util.List;

public class AnnotatedBeanCustomizer extends ProductsCustomizer {
    private Class<?>[] classes;

    public AnnotatedBeanCustomizer(Class<?>... classes) {
        this.classes = classes;
    }

    @Override
    public void customizeCamelK() {
        customizeQuarkus();
    }

    @Override
    public void customizeQuarkus() {
        // quarkus 3 migrated to jakarta ee
        final String packageName = QuarkusConfiguration.quarkusVersion().startsWith("2") ? "javax" : "jakarta";
        for (Class<?> clazz : classes) {
            CompilationUnit compilationUnit = getIntegrationBuilder().getCompilationUnit(clazz);
            AnnotationUtils.addAnnotationsToClass(
                compilationUnit, List.of(packageName + ".enterprise.context.ApplicationScoped", packageName + ".inject.Named"),
                List.of("ApplicationScoped", "Named"));

            getIntegrationBuilder().addClass(compilationUnit);
        }
    }

    @Override
    public void customizeSpringboot() {
        for (Class<?> clazz : classes) {
            CompilationUnit compilationUnit = getIntegrationBuilder().getCompilationUnit(clazz);
            getIntegrationBuilder().getRouteBuilder().ifPresent(
                cu -> cu.getPackageDeclaration().ifPresent(compilationUnit::setPackageDeclaration)
            );

            AnnotationUtils.addAnnotationsToClass(
                compilationUnit, List.of("org.springframework.stereotype.Component"), List.of("Component"));

            getIntegrationBuilder().addClass(compilationUnit);
        }
    }
}
