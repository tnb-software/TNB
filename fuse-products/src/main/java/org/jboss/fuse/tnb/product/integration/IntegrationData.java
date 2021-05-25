package org.jboss.fuse.tnb.product.integration;

import java.util.List;
import java.util.Properties;

/**
 * Holds the integration source code and properties file for Camel-K.
 */
public class IntegrationData {
    private final String integration;
    private final Properties properties;
    private final List<String> dependencies;

    public IntegrationData(String integration, Properties properties, List<String> dependencies) {
        this.integration = integration;
        this.properties = properties;
        this.dependencies = dependencies;
    }

    public String getIntegration() {
        return integration;
    }

    public Properties getProperties() {
        return properties;
    }

    public List<String> getDependencies() {
        return dependencies;
    }
}
