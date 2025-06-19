package software.tnb.db.common.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

import java.util.HashMap;
import java.util.Map;

public class SQLConfiguration extends ServiceConfiguration {

    private static final String ENV_VARIABLES = "sql.env.vars";

    public SQLConfiguration environmentVariables(Map<String, String> env) {
        set(ENV_VARIABLES, env);
        return this;
    }

    public SQLConfiguration addEnvironmentVariable(String name, String value) {
        Map<String, String> env = new HashMap<>(getEnvironmentVariables());
        env.put(name, value);
        environmentVariables(env);
        return this;
    }

    public Map<String, String> getEnvironmentVariables() {
        return get(ENV_VARIABLES, Map.class);
    }
}
