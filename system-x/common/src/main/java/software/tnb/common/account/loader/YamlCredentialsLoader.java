package software.tnb.common.account.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Map;

public class YamlCredentialsLoader extends CredentialsLoader {
    private static final Logger LOG = LoggerFactory.getLogger(YamlCredentialsLoader.class);

    private final Map<String, Map<String, Object>> credentials;

    public YamlCredentialsLoader(String credentialsFile) throws Exception {
        try (FileInputStream fs = new FileInputStream(Paths.get(credentialsFile).toAbsolutePath().toString())) {
            LOG.info("Loading credentials file from {}", Paths.get(credentialsFile).toAbsolutePath());
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
