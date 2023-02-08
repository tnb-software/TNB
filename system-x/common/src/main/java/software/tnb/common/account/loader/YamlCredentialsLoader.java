package software.tnb.common.account.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

public class YamlCredentialsLoader extends CredentialsLoader {
    private static final Logger LOG = LoggerFactory.getLogger(YamlCredentialsLoader.class);

    private final Map<String, Map<String, Object>> credentials;

    public YamlCredentialsLoader(String credentialsAsString) throws Exception {
        credentials = (Map) ((Map) new Yaml().load(credentialsAsString)).get("services");
    }

    public YamlCredentialsLoader(File credentialsFile) throws Exception {
        try (FileInputStream fs = new FileInputStream(credentialsFile.getAbsolutePath())) {
            LOG.info("Loading credentials file from {}", credentialsFile.getAbsolutePath());
            credentials = (Map) ((Map) new Yaml().load(fs)).get("services");
        }
    }

    @Override
    public Object loadCredentials(String credentialsId) {
        return credentials.getOrDefault(credentialsId, Map.of()).getOrDefault("credentials", null);
    }

    @Override
    public String toJson(Object credentials) {
        try {
            return mapper.writeValueAsString(credentials);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to convert credentials to json", e);
        }
    }
}
