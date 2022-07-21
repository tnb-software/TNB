package software.tnb.product.customizer.component.saga;

import software.tnb.product.customizer.component.rest.RestCustomizer;
import software.tnb.product.util.maven.Maven;

import java.util.Map;

public class SagaLRACustomizer extends RestCustomizer {

    private final String lraCoordinatorUrl;
    private final String lraLocalParticipantUrl;

    public SagaLRACustomizer(final String lraCoordinatorUrl, final String lraLocalParticipantUrl) {
        this.lraCoordinatorUrl = lraCoordinatorUrl;
        this.lraLocalParticipantUrl = lraLocalParticipantUrl;
    }

    @Override
    public void customizeQuarkus() {
        getIntegrationBuilder().addToProperties("quarkus.http.root-path", "/camel");
        getIntegrationBuilder().addToProperties(getCommonProperties());
        getIntegrationBuilder().dependencies("lra");
        getIntegrationBuilder().dependencies(Maven.createDependency("io.quarkus:quarkus-resteasy"));
    }

    @Override
    public void customizeSpringboot() {
        super.customizeSpringboot();
        getIntegrationBuilder().addToProperties(getCommonProperties());
        getIntegrationBuilder().dependencies("lra", "undertow", "servlet");
    }

    private Map<String, String> getCommonProperties() {
        return Map.of(
            "camel.service.lra.enabled", "true"
            , "camel.lra.enabled", "true"
            , "camel.lra.coordinator-url", lraCoordinatorUrl
            , "camel.lra.local-participant-url", lraLocalParticipantUrl
        );
    }
}
