package org.jboss.fuse.tnb.customizer;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.Properties;

/**
 * Abstract class for all customizations for integration class / customize method / application properties.
 */
public abstract class Customizer {
    private MethodSpec.Builder configureMethodBuilder;
    private TypeSpec.Builder routeBuilderClassBuilder;
    private Properties applicationProperties;

    public abstract void customize();

    public MethodSpec.Builder getConfigureMethodBuilder() {
        return configureMethodBuilder;
    }

    public void setConfigureMethodBuilder(MethodSpec.Builder configureMethodBuilder) {
        this.configureMethodBuilder = configureMethodBuilder;
    }

    public TypeSpec.Builder getRouteBuilderClassBuilder() {
        return routeBuilderClassBuilder;
    }

    public void setRouteBuilderClassBuilder(TypeSpec.Builder routeBuilderClassBuilder) {
        this.routeBuilderClassBuilder = routeBuilderClassBuilder;
    }

    public Properties getApplicationProperties() {
        return applicationProperties;
    }

    public void setApplicationProperties(Properties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }
}
