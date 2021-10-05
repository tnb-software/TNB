package org.jboss.fuse.tnb.product.integration;

import java.util.List;
import java.util.Properties;

/**
 * Holds the integration source code and properties file for Camel-K.
 */
public class IntegrationData {
    private final String sourceName;
    private final List<Resource> resources;
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

    public IntegrationData(String sourceName, String integration, Properties properties, String secretName, List<Resource> resources) {
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

    public List<Resource> getResources() {
        return resources;
    }

    public static class Resource {
        private ResourceType type;
        private String name;
        private String content;

        public Resource(ResourceType type, String name, String content) {
            this.name = name;
            this.type = type;
            this.content = content;
        }

        public ResourceType getType() {
            return type;
        }

        public void setType(ResourceType type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public enum ResourceType {
        DATA("data"),
        CONFIG("config"),
        OPENAPI("openapi");

        private final String value;

        ResourceType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
