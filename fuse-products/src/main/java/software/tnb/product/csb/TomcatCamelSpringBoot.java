package software.tnb.product.csb;

import org.apache.maven.model.Dependency;

import software.tnb.product.LocalProduct;
import software.tnb.product.Product;
import software.tnb.product.application.App;
import software.tnb.product.csb.application.TomcatSpringBootApp;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.customizer.component.rest.RestCustomizer;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

import com.google.auto.service.AutoService;

import software.tnb.product.util.maven.Maven;

import java.util.List;
import java.util.stream.Collectors;

@AutoService(Product.class)
public class TomcatCamelSpringBoot extends LocalProduct {

    private TomcatSpringBootApp app;

    @Override
    public App createIntegrationApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        // Let's remove restcustomizer, since it exclude tomcat
        List<Customizer> customizers = integrationBuilder.getCustomizers()
            .stream().filter(customizer -> customizer instanceof RestCustomizer)
            .collect(Collectors.toList());
        integrationBuilder.getCustomizers().remove(customizers);

        // spring web is needed and tomcat is provided
        Dependency providedTomcatDependency = new Dependency();
        providedTomcatDependency.setArtifactId("spring-boot-starter-tomcat");
        providedTomcatDependency.setGroupId("org.springframework.boot");
        providedTomcatDependency.setScope("provided");

        integrationBuilder.dependencies(
            Maven.createDependency("org.springframework.boot:spring-boot-starter-web"),
            providedTomcatDependency);

        app = new TomcatSpringBootApp(integrationBuilder);
        return app;
    }

    @Override
    public void setupProduct() {
        super.setupProduct();
    }

    @Override
    public void teardownProduct() {
        app.stop();
    }
}
