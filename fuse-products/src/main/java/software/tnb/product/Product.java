package software.tnb.product;

import software.tnb.product.application.App;
import software.tnb.product.integration.builder.AbstractIntegrationBuilder;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Product implements BeforeAllCallback, AfterAllCallback {
    protected Map<String, App> integrations = Collections.synchronizedMap(new LinkedHashMap());

    public App createIntegration(AbstractIntegrationBuilder<?> integrationBuilder) {
        if (integrations.containsKey(integrationBuilder.getIntegrationName())) {
            throw new IllegalArgumentException("Integration with name " + integrationBuilder.getIntegrationName() + " is already running!");
        }
        try {
            App app = createIntegrationApp(integrationBuilder);
            integrations.put(integrationBuilder.getIntegrationName(), app);
            app.start();
            app.waitUntilReady();
            return app;
        } catch (Exception e) {
            // Print the stackstace as it is swallowed by junit somehow
            e.printStackTrace();
            throw e;
        }
    }

    public Map<String, App> createIntegration(AbstractIntegrationBuilder<?> integrationBuilder, AbstractIntegrationBuilder<?>... other) {
        // Return only integrations created in this invocation, not all created integrations
        List<AbstractIntegrationBuilder<?>> integrationBuilders = new ArrayList<>();
        integrationBuilders.add(integrationBuilder);
        integrationBuilders.addAll(Arrays.asList(other));
        return integrationBuilders.stream().collect(Collectors.toMap(AbstractIntegrationBuilder::getIntegrationName, this::createIntegration));
    }

    protected abstract App createIntegrationApp(AbstractIntegrationBuilder<?> integrationBuilder);

    public void removeIntegrations() {
        List<App> integrationsList = new ArrayList<>(integrations.values());
        Collections.reverse(integrationsList);
        integrationsList.forEach(App::stop);
        integrations.clear();
    }

    public Map<String, App> getIntegrations() {
        return integrations;
    }

    public abstract void setupProduct();

    public abstract void teardownProduct();
}
