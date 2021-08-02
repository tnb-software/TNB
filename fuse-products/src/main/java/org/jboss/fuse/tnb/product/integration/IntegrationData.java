package org.jboss.fuse.tnb.product.integration;

import java.util.Map;
import java.util.Properties;

/**
 * Holds the integration source code and properties file for Camel-K.
 */
public class IntegrationData {
    private final String sourceName;
    private final Map<String, String> resources;
    private final String integration;
    private final Properties properties;
    private final String secretName;

    public IntegrationData(String integration, Properties properties) {
        this("MyRouteBuilder.java", integration, properties);
    }

    public IntegrationData(String sourceName, String integration, Properties properties) {
        this(sourceName, integration, properties, null, null);
    }

    public IntegrationData(String sourceName, String integration, Properties properties, String secretName) {
        this(sourceName, integration, properties, secretName, null);
    }

    public IntegrationData(String sourceName, String integration, Properties properties, String secretName, Map<String, String> resources) {
        this.integration = integration;
        this.properties = properties;
        this.sourceName = sourceName;
        this.secretName = secretName;
        this.resources = resources;
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

    public Map<String, String> getResources() {
        return resources;
    }
}
