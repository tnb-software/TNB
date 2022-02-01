package org.jboss.fuse.tnb.product;

import org.jboss.fuse.tnb.product.application.App;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class Product implements BeforeAllCallback, AfterAllCallback {
    protected Map<String, App> integrations = new ConcurrentHashMap<>();

    public App createIntegration(IntegrationBuilder integrationBuilder) {
        if (integrations.containsKey(integrationBuilder.getIntegrationName())) {
            throw new IllegalArgumentException("Integration with name " + integrationBuilder.getIntegrationName() + " is already running!");
        }
        App app = createIntegrationApp(integrationBuilder);
        integrations.put(integrationBuilder.getIntegrationName(), app);
        app.start();
        app.waitUntilReady();
        return app;
    }

    public Map<String, App> createIntegration(IntegrationBuilder integrationBuilder, IntegrationBuilder... other) {
        // Return only integrations created in this invocation, not all created integrations
        List<IntegrationBuilder> integrationBuilders = new ArrayList<>();
        integrationBuilders.add(integrationBuilder);
        integrationBuilders.addAll(Arrays.asList(other));
        return integrationBuilders.stream().collect(Collectors.toMap(IntegrationBuilder::getIntegrationName, this::createIntegration));
    }

    protected abstract App createIntegrationApp(IntegrationBuilder integrationBuilder);

    public void removeIntegrations() {
        integrations.values().forEach(App::stop);
        integrations.clear();
    }

    public Map<String, App> getIntegrations() {
        return integrations;
    }

    public abstract void setupProduct();

    public abstract void teardownProduct();
}
