package org.jboss.fuse.tnb.product.customizer.component.bean;

import org.jboss.fuse.tnb.product.customizer.ProductsCustomizer;
import org.jboss.fuse.tnb.product.util.jparser.AnnotationUtils;

import com.github.javaparser.ast.CompilationUnit;

import java.util.List;

public class AnnotatedBeanCustomizer extends ProductsCustomizer {

    private Class[] classes;

    public AnnotatedBeanCustomizer(Class... classes) {
        this.classes = classes;
    }

    @Override
    public void customizeCamelK() {
    }

    @Override
    public void customizeQuarkus() {
        for (Class clazz : classes) {
            CompilationUnit compilationUnit = getIntegrationBuilder().getCompilationUnit(clazz);
            AnnotationUtils.addAnnotationsToClass(
                compilationUnit, List.of("javax.enterprise.context.ApplicationScoped", "javax.inject.Named"),
                List.of("ApplicationScoped", "Named"));

            getIntegrationBuilder().addClass(compilationUnit);
        }
    }

    @Override
    public void customizeSpringboot() {
        for (Class clazz : classes) {
            CompilationUnit compilationUnit = getIntegrationBuilder().getCompilationUnit(clazz);
            getIntegrationBuilder().getRouteBuilder().ifPresent(
                cu -> cu.getPackageDeclaration().ifPresent(
                    pd -> compilationUnit.setPackageDeclaration(pd)
                )
            );

            AnnotationUtils.addAnnotationsToClass(
                compilationUnit, List.of("org.springframework.stereotype.Component"), List.of("Component"));

            getIntegrationBuilder().addClass(compilationUnit);
        }
    }
}
