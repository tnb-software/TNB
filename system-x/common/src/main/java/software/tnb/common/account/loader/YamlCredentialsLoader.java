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

    public YamlCredentialsLoader(String credentialsAsString, String rootKey) {
        credentials = loadCredentialsFromYaml(new Yaml().load(credentialsAsString), rootKey);
    }

    public YamlCredentialsLoader(File credentialsFile, String rootKey) {
        try (FileInputStream fs = new FileInputStream(credentialsFile.getAbsolutePath())) {
            LOG.trace("Loading credentials file from {}", credentialsFile.getAbsolutePath());
            credentials = loadCredentialsFromYaml(new Yaml().load(fs), rootKey);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load credentials from file", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<String, Object>> loadCredentialsFromYaml(Object yamlContent, String rootKey) {
        if (yamlContent instanceof Map m) {
            if (rootKey != null && !rootKey.isEmpty()) {
                yamlContent = m.get(rootKey);
            }
            return (Map) yamlContent;
        } else {
            throw new RuntimeException("YAML content was not an instance of a map, was: " + yamlContent);
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
