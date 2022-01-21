package org.jboss.fuse.tnb.product.csb.customizer;

import org.jboss.fuse.tnb.product.integration.Customizer;

import org.springframework.stereotype.Component;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

/**
 * For camel spring boot we add @Component to RouteBuilder class
 */
public class ComponentCustomizer extends Customizer {
    @Override
    public void customize() {
        getIntegrationBuilder().getRouteBuilder().accept(new ModifierVisitor<>() {
            @Override
            public Visitable visit(ClassOrInterfaceDeclaration n, Object arg) {
                super.visit(n, arg);
                if (n.getExtendedTypes().stream().anyMatch(x -> x.getNameAsString().equals("RouteBuilder"))) {
                    n.addAnnotation(Component.class);
                }
                return n;
            }
        }, null);
    }
}
