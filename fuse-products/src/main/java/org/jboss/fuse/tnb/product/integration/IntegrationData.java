package org.jboss.fuse.tnb.product.integration;

import java.util.Properties;

/**
 * Holds the integration source code and properties file for Camel-K.
 */
public class IntegrationData {
    private final String sourceName;
    private final String integration;
    private final Properties properties;
    private final String secretName;

    public IntegrationData(String integration, Properties properties) {
        this("MyRouteBuilder.java", integration, properties);
    }

    public IntegrationData(String sourceName, String integration, Properties properties) {
        this(sourceName, integration, properties, null);
    }

    public IntegrationData(String sourceName, String integration, Properties properties, String secretName) {
        this.integration = integration;
        this.properties = properties;
        this.sourceName = sourceName;
        this.secretName = secretName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getIntegration() {
        return integration;
    }

    public Properties getProperties() {
        return properties;
    }

    public String getSecretName() {
        return secretName;
    }
}
