package software.tnb.product.cxf.app;

import software.tnb.common.utils.WaitUtils;
import software.tnb.product.cq.application.OpenshiftQuarkusApp;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

public class PlainQuarkusApp extends OpenshiftQuarkusApp {
    public PlainQuarkusApp(AbstractIntegrationBuilder<?> integrationBuilder) {
        super(integrationBuilder);
    }

    @Override
    public void waitUntilReady() {
        if (shouldRun()) {
            WaitUtils.waitFor(() -> isReady(), this::isFailed, 1000L, "Waiting until the integration " + name + " is running");
            started = true;
        }
    }
}
