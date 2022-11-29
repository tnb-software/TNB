package software.tnb.product.customizer.component.rest;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.product.ck.customizer.IntegrationSpecCustomizer;
import software.tnb.product.customizer.ProductsCustomizer;
import software.tnb.product.util.maven.Maven;

import java.util.List;
import java.util.Map;

import io.fabric8.camelk.v1.IntegrationSpecBuilder;

public class RestCustomizer extends ProductsCustomizer implements IntegrationSpecCustomizer {

    @Override
    public void customizeCamelK() {
        customizeQuarkus();
    }

    @Override
    public void customizeQuarkus() {
        getIntegrationBuilder().addToProperties("quarkus.camel.servlet.url-patterns", "/camel/*");
        getIntegrationBuilder().addToProperties("quarkus.openshift.route.expose", "true");
        getIntegrationBuilder().dependencies("rest");
    }

    @Override
    public void customizeSpringboot() {
        if (!OpenshiftConfiguration.isOpenshift()) {
            getIntegrationBuilder().dependencies(Maven.createDependency("org.springframework.boot:spring-boot-starter-web",
                    "org.springframework.boot:spring-boot-starter-tomcat")
                )
                .dependencies(
                    Maven.createDependency("org.springframework.boot:spring-boot-starter-undertow")
                );
        }
    }

    @Override
    public void customizeIntegration(IntegrationSpecBuilder integrationSpecBuilder) {
        Map<String, Object> configuration = Map.of("properties", List.of("quarkus.camel.servlet.url-patterns=/camel/*"));
        mergeTraitConfiguration(integrationSpecBuilder, "builder", configuration);
    }
}
