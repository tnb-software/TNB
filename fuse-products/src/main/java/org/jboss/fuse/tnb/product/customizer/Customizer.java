package org.jboss.fuse.tnb.product.customizer;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * Abstract class for customizations for routebuilder / application properties.
 */
public abstract class Customizer {
    protected static final Logger LOG = LoggerFactory.getLogger(Customizer.class);

    private ProductType product;

    public Customizer() {
    }

    public Customizer(ProductType product) {
        this.product = product;
    }

    private IntegrationBuilder integrationBuilder;

    public void doCustomize() {
        if (product == null || TestConfiguration.product() == product) {
            LOG.trace("Running {} customizer {}", product == null ? "common" : product.name(), this.getClass().getSimpleName());
            customize();
        } else {
            LOG.trace("Skipping {} customizer {}, current product is {}", product.name(),
                this.getClass().getSimpleName().isEmpty() ? "<dynamic>" : this.getClass().getSimpleName(),
                TestConfiguration.product().name());
        }
    }

    public abstract void customize();

    public IntegrationBuilder getIntegrationBuilder() {
        return integrationBuilder;
    }

    public ClassOrInterfaceDeclaration getRouteBuilderClass() {
        return getIntegrationBuilder().getRouteBuilder().getClassByName(IntegrationBuilder.ROUTE_BUILDER_NAME)
            .orElseThrow(() -> new IllegalArgumentException("There is no class with name " + IntegrationBuilder.ROUTE_BUILDER_NAME));
    }

    public MethodDeclaration getConfigureMethod() {
        return getRouteBuilderClass().getMethodsBySignature(IntegrationBuilder.ROUTE_BUILDER_METHOD_NAME).get(0);
    }

    public void setIntegrationBuilder(IntegrationBuilder integrationBuilder) {
        this.integrationBuilder = integrationBuilder;
    }
}
