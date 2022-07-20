package software.tnb.product.customizer.component.saga;

import software.tnb.product.customizer.component.rest.RestCustomizer;

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
        super.customizeQuarkus();
        getIntegrationBuilder().addToProperties(getCommonProperties());
        getIntegrationBuilder().dependencies("lra");
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
