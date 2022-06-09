package software.tnb.common.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Map;

public class YamlCredentialsLoader implements CredentialsLoader {
    private static final Logger LOG = LoggerFactory.getLogger(YamlCredentialsLoader.class);

    private final Map<String, Map<String, Object>> credentials;
    private final ObjectMapper mapper;

    public YamlCredentialsLoader(String credentialsFile) throws Exception {
        try (FileInputStream fs = new FileInputStream(Paths.get(credentialsFile).toAbsolutePath().toString())) {
            LOG.info("Loading credentials file from {}", Paths.get(credentialsFile).toAbsolutePath());
            credentials = (Map) ((Map) new Yaml().load(fs)).get("services");
            mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
    }

    @Override
    public <T extends Account> T get(String credentialsId, Class<T> accountClass) {
        if (!credentials.containsKey(credentialsId)) {
            return null;
        }
        return accountClass.cast(mapper.convertValue(credentials.get(credentialsId).get("credentials"), accountClass));
    }
}
