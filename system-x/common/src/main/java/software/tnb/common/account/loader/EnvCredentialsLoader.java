package software.tnb.common.account.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Loads account credentials from environment variables.
 */
public class EnvCredentialsLoader extends CredentialsLoader {
    private static final Logger LOG = LoggerFactory.getLogger(EnvCredentialsLoader.class);
    private static final String ENV_PREFIX = "TNB_";
    private static final Pattern SNAKE_CASE_REPLACEMENT_PATTERN = Pattern.compile("_([a-z])");

    @Override
    public Object loadCredentials(String credentialsId) {
        String prefix = ENV_PREFIX + credentialsId.toUpperCase().replace("-", "_") + "_";
        Map<String, Object> credentials = new HashMap<>();

        LOG.trace("Looking for environment variables with prefix: {}", prefix);

        System.getenv().entrySet().stream()
            .filter(e -> e.getKey().startsWith(prefix))
            .forEach(e -> {
                String envSuffix = e.getKey().substring(prefix.length());
                // convert snake_case to camelCase if present
                String fieldName = SNAKE_CASE_REPLACEMENT_PATTERN.matcher(envSuffix.toLowerCase()).replaceAll(m -> m.group(1).toUpperCase());
                // use Yaml().load to either load a string or a map
                credentials.put(fieldName, new Yaml().load(e.getValue()));
                LOG.trace("Loaded env var {} into field {}", e.getKey(), fieldName);
            });

        if (credentials.isEmpty()) {
            LOG.trace("No environment variables found for credentials id: {}", credentialsId);
            return null;
        }

        LOG.debug("Loaded {} fields from environment variables for credentials id: {}", credentials.size(), credentialsId);
        return credentials;
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
