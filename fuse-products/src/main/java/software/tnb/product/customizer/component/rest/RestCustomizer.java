package software.tnb.product.customizer.component.rest;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.product.customizer.ProductsCustomizer;
import software.tnb.product.util.maven.Maven;

public class RestCustomizer extends ProductsCustomizer {
    private static final String DEFAULT_PATH = "/camel";

    @Override
    public void customizeQuarkus() {
        getIntegrationBuilder().addToApplicationProperties("quarkus.camel.servlet.url-patterns", DEFAULT_PATH + "/*");
        getIntegrationBuilder().addToApplicationProperties("quarkus.openshift.route.expose", "true");
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

    public String getReadinessCheckPath() {
        return DEFAULT_PATH + "/";
    }
}
