package software.tnb.product.customizer;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.product.ProductType;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.Optional;

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

    private AbstractIntegrationBuilder<?> integrationBuilder;

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

    public AbstractIntegrationBuilder<?> getIntegrationBuilder() {
        return integrationBuilder;
    }

    /**
     * Gets the specified routebuilder class.
     * @param name name of the routebuilder class
     * @return {@link ClassOrInterfaceDeclaration} instance
     */
    public ClassOrInterfaceDeclaration getRouteBuilderClass(String name) {
        for (CompilationUnit routeBuilder : getIntegrationBuilder().getRouteBuilders()) {
            final Optional<ClassOrInterfaceDeclaration> coid = routeBuilder.getClassByName(name);
            if (coid.isPresent()) {
                return coid.get();
            }
        }
        throw new IllegalArgumentException("There is no class with name " + name + " in defined RouteBuilders");
    }

    public MethodDeclaration getConfigureMethod(String className) {
        return getRouteBuilderClass(className).getMethodsBySignature(AbstractIntegrationBuilder.ROUTE_BUILDER_METHOD_NAME).get(0);
    }

    public void setIntegrationBuilder(AbstractIntegrationBuilder<?> integrationBuilder) {
        this.integrationBuilder = integrationBuilder;
    }
}
