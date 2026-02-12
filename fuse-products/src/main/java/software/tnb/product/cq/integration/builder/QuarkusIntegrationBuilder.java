package software.tnb.product.cq.integration.builder;

import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

public class QuarkusIntegrationBuilder extends AbstractIntegrationBuilder<QuarkusIntegrationBuilder> {
    private boolean devMode = false;

    public QuarkusIntegrationBuilder(String integrationName) {
        super(integrationName);
    }

    @Override
    protected QuarkusIntegrationBuilder self() {
        return this;
    }

    /**
     * Runs the app using quarkus:dev
     * @return this
     */
    public QuarkusIntegrationBuilder useDevMode() {
        this.devMode = true;
        return this;
    }

    public boolean isDevMode() {
        return devMode;
    }
}
