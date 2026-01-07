package software.tnb.product.cxf.app;

import software.tnb.product.cq.application.OpenshiftQuarkusApp;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

public class PlainQuarkusApp extends OpenshiftQuarkusApp {
    public PlainQuarkusApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);
    }
}
