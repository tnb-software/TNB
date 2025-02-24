package software.tnb.product.customizer.component.rest;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.product.customizer.ProductsCustomizer;
import software.tnb.product.util.maven.Maven;

public class RestCustomizer extends ProductsCustomizer {
    private static final String DEFAULT_PATH = "/camel";
    private final String path;

    public RestCustomizer() {
        this.path = DEFAULT_PATH;
    }

    // Needed to override the path when using platform-http and not servlet
    public RestCustomizer(String path) {
        this.path = path;
    }

    @Override
    public void customizeQuarkus() {
        getIntegrationBuilder().addToApplicationProperties("quarkus.camel.servlet.url-patterns", path + "/*");
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
        return "/".equals(path) ? path : path + "/";
    }
}
