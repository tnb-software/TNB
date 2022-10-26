package software.tnb.product.csb.integration.builder;

import software.tnb.product.integration.Resource;
import software.tnb.product.integration.builder.AbstractMavenGitIntegrationBuilder;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Integration builder adding methods related to springboot only.
 *
 * It is marked final on purpose, if there is ever a case when you'll want to subclass this, you need to make this class generic
 * (similar to parent Git/MavenGit IntegrationBuilder) to be able to use the fluent builder
 */
public final class SpringBootIntegrationBuilder extends AbstractMavenGitIntegrationBuilder<SpringBootIntegrationBuilder> {
    private final List<Resource> xmlCamelContext = new ArrayList<>();

    public SpringBootIntegrationBuilder(String integrationName) {
        super(integrationName);
        withFinalName(integrationName);
    }

    protected SpringBootIntegrationBuilder self() {
        return this;
    }

    public SpringBootIntegrationBuilder fromSpringBootXmlCamelContext(String camelContextPath) {
        String resourceData;
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(camelContextPath)) {
            resourceData = IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read resource: ", e);
        }

        xmlCamelContext.add(new Resource(camelContextPath.substring(camelContextPath.lastIndexOf(File.separator) + 1), resourceData));
        return self();
    }

    public List<Resource> getXmlCamelContext() {
        return xmlCamelContext;
    }
}
