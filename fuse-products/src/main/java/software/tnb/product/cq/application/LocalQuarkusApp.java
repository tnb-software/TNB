package software.tnb.product.cq.application;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.customizer.component.rest.RestCustomizer;
import software.tnb.product.endpoint.Endpoint;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

import java.util.Optional;

public abstract class LocalQuarkusApp extends QuarkusApp {
    public LocalQuarkusApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);

        endpoint = new Endpoint(() -> "http://localhost:" + integrationBuilder.getPort());

        Optional<Customizer> restCustomizer = integrationBuilder.getCustomizers().stream().filter(c -> c instanceof RestCustomizer).findFirst();
        // For local quarkus app, the HTTP request will fail when the endpoint is not ready, so check if an exception was raised or not
        restCustomizer.ifPresent(customizer ->
            readinessCheck = () -> {
                try {
                    HTTPUtils.getInstance().get(getEndpoint() + ((RestCustomizer) customizer).getReadinessCheckPath());
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        );
    }
}
