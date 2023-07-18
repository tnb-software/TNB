package software.tnb.apicurio.registry.validation;

import software.tnb.common.validation.Validation;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import io.apicurio.registry.rest.client.RegistryClient;

public class ApicurioRegistryValidation implements Validation {
    private final RegistryClient client;

    public ApicurioRegistryValidation(RegistryClient client) {
        this.client = client;
    }

    public void createSchema(String groupId, String artifactId, String schema) {
        try (InputStream is = IOUtils.toInputStream(schema, Charset.defaultCharset())) {
            createSchema(groupId, artifactId, is);
        } catch (IOException e) {
            throw new RuntimeException("Unable to convert string to inputstream", e);
        }
    }

    public void createSchema(String groupId, String artifactId, InputStream schema) {
        client.createArtifact(groupId, artifactId, schema);
    }
}
