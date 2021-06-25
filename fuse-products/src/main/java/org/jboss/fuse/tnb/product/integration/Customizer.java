package org.jboss.fuse.tnb.product.integration;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.Properties;

/**
 * Abstract class for customizations for routebuilder / application properties.
 */
public abstract class Customizer {

    private IntegrationBuilder integrationBuilder;

    public abstract void customize();

    public Properties getApplicationProperties() {
        return getIntegrationBuilder().getAppProperties();
    }

    public CompilationUnit getRouteBuilder() {
        return getIntegrationBuilder().getRouteBuilder();
    }

    public IntegrationBuilder getIntegrationBuilder() {
        return integrationBuilder;
    }

    public ClassOrInterfaceDeclaration getRouteBuilderClass() {
        return getRouteBuilder().getClassByName("MyRouteBuilder").get();
    }

    public MethodDeclaration getConfigureMethod() {
        return getRouteBuilderClass().getMethodsBySignature("configure").get(0);
    }

    public void setIntegrationBuilder(IntegrationBuilder integrationBuilder) {
        this.integrationBuilder = integrationBuilder;
    }
}
