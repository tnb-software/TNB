package org.jboss.fuse.tnb.product.cq.utils;

import org.jboss.fuse.tnb.product.integration.Customizer;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

import javax.enterprise.context.ApplicationScoped;

/**
 * For camel quarkus we add @ApplicationScoped to RouteBuilder class
 */
public class ApplicationScopeCustomizer extends Customizer {
    @Override
    public void customize() {
        getIntegrationBuilder().getRouteBuilder().accept(new ModifierVisitor<>() {
            @Override
            public Visitable visit(ClassOrInterfaceDeclaration n, Object arg) {
                super.visit(n, arg);
                if (n.getExtendedTypes().stream().anyMatch(x -> x.getNameAsString().equals("RouteBuilder"))) {
                    n.addAnnotation(ApplicationScoped.class);
                }
                return n;
            }
        }, null);
    }
}
