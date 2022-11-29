package software.tnb.product.customizer.component.cxf;

import software.tnb.product.customizer.ProductsCustomizer;
import software.tnb.product.cxf.CxfConfiguration;
import software.tnb.product.util.maven.Maven;

public class CxfRestCustomizer extends ProductsCustomizer {

    @Override
    public void customizeCamelK() {
    }

    @Override
    public void customizeQuarkus() {
    }

    @Override
    public void customizeSpringboot() {
        getIntegrationBuilder().dependencies(
                Maven.createDependency("org.apache.cxf:cxf-spring-boot-starter-jaxrs:" + CxfConfiguration.cxfVersion(),
                    "org.springframework.boot:spring-boot-starter-tomcat")
            )
            .dependencies(
                Maven.createDependency("org.apache.camel.springboot:camel-cxf-rest-starter")
            );
    }
}
