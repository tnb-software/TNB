package org.jboss.fuse.tnb.customizer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.Properties;

/**
 * Abstract class for customizations for routebuilder / application properties
 */
public abstract class Customizer {

    private CompilationUnit routeBuilder;
    private Properties applicationProperties;

    public abstract void customize();

    public Properties getApplicationProperties() {
        return applicationProperties;
    }

    public CompilationUnit getRouteBuilder() {
        return routeBuilder;
    }

    public ClassOrInterfaceDeclaration getRouteBuilderClass() {
        return getRouteBuilder().getClassByName("MyRouteBuilder").get();
    }

    public MethodDeclaration getConfigureMethod() {
        return getRouteBuilderClass().getMethodsBySignature("configure").get(0);
    }

    public void setRouteBuilder(CompilationUnit compilationUnit) {
        this.routeBuilder = compilationUnit;
    }

    public void setApplicationProperties(Properties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }
}
