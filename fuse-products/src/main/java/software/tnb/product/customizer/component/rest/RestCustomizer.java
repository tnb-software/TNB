package software.tnb.product.customizer.component.rest;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.product.ck.customizer.IntegrationSpecCustomizer;
import software.tnb.product.customizer.ProductsCustomizer;
import software.tnb.product.util.maven.Maven;

import org.apache.camel.v1.IntegrationSpec;
import org.apache.camel.v1.integrationspec.Traits;
import org.apache.camel.v1.integrationspec.traits.Builder;

import java.util.ArrayList;
import java.util.List;

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
    public void customizeIntegration(IntegrationSpec integrationSpec) {
        final Traits traits = integrationSpec.getTraits() == null ? new Traits() : integrationSpec.getTraits();
        final Builder builder = traits.getBuilder() == null ? new Builder() : traits.getBuilder();
        final List<String> properties = builder.getProperties() == null ? new ArrayList<>() : builder.getProperties();
        properties.add("quarkus.camel.servlet.url-patterns=/camel/*");
        builder.setProperties(properties);
        traits.setBuilder(builder);
        integrationSpec.setTraits(traits);
    }
}
