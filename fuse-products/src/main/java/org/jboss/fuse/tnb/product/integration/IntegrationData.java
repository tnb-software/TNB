package org.jboss.fuse.tnb.product.integration;

import java.util.Properties;

/**
 * Holds the integration source code and properties file for Camel-K.
 */
public class IntegrationData {
    private final String integration;
    private final Properties properties;

    public IntegrationData(String integration, Properties properties) {
        this.integration = integration;
        this.properties = properties;
    }

    public String getIntegration() {
        return integration;
    }

    public Properties getProperties() {
        return properties;
    }
}
