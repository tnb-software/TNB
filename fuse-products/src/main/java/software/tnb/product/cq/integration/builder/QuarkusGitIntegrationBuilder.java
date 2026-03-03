package software.tnb.product.cq.integration.builder;

import software.tnb.product.integration.builder.AbstractMavenGitIntegrationBuilder;

public class QuarkusGitIntegrationBuilder extends AbstractMavenGitIntegrationBuilder<QuarkusGitIntegrationBuilder> {
    public QuarkusGitIntegrationBuilder(String name) {
        super(name);
    }

    @Override
    protected QuarkusGitIntegrationBuilder self() {
        return this;
    }
}
