package org.jboss.fuse.tnb.product.ck.integration.builder;

import org.jboss.fuse.tnb.common.utils.IOUtils;
import org.jboss.fuse.tnb.product.ck.customizer.ModelineCustomizer;
import org.jboss.fuse.tnb.product.ck.integration.resource.CamelKResource;
import org.jboss.fuse.tnb.product.ck.integration.resource.ResourceType;
import org.jboss.fuse.tnb.product.integration.builder.AbstractIntegrationBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import io.fabric8.kubernetes.api.model.Secret;

/**
 * Integration builder adding methods related to camel-k only.
 *
 * It is marked final on purpose, if there is ever a case when you'll want to subclass this, you need to make this class generic
 * (similar to parent Git/MavenGit IntegrationBuilder) to be able to use the fluent builder
 */
public final class CamelKIntegrationBuilder extends AbstractIntegrationBuilder<CamelKIntegrationBuilder> {
    private String content;
    private String secret;

    public CamelKIntegrationBuilder(String integrationName) {
        super(integrationName);
    }

    @Override
    protected CamelKIntegrationBuilder self() {
        return this;
    }

    public CamelKIntegrationBuilder fromFile(Path path) {
        this.content = IOUtils.readFile(path);
        return fileName(path.getFileName().toFile().getName());
    }

    public CamelKIntegrationBuilder fromString(String content) {
        this.content = content;
        return self();
    }

    public CamelKIntegrationBuilder secret(String secret) {
        this.secret = secret;
        return self();
    }

    public CamelKIntegrationBuilder secret(Secret secret) {
        this.secret = secret.getMetadata().getName();
        return self();
    }

    /**
     * Add classpath resource (represented by string path) into integration and the resource type
     *
     * @param resource path to the resource
     * @param type resource type
     * @return this
     */
    public CamelKIntegrationBuilder addClasspathResource(ResourceType type, String resource) {
        String resourceData;
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource)) {
            resourceData = org.apache.commons.io.IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read resource: ", e);
        }
        return addResource(new CamelKResource(type, resource, resourceData));
    }

    public CamelKIntegrationBuilder addResource(ResourceType type, String resource, String content) {
        return super.addResource(new CamelKResource(type, resource, content));
    }

    public CamelKIntegrationBuilder addClasspathResource(String resource) {
        addClasspathResource(ResourceType.DATA, resource);
        return self();
    }

    public CamelKIntegrationBuilder addToModeline(String modeline) {
        return addCustomizer(new ModelineCustomizer(modeline));
    }

    public String getSecret() {
        return secret;
    }

    public String getContent() {
        return content;
    }
}
