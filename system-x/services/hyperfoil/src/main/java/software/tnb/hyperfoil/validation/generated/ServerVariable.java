package software.tnb.hyperfoil.validation.generated;

import java.util.HashSet;

/**
 * Representing a Server Variable for server URL template substitution.
 */
public class ServerVariable {
    private String description;
    private String defaultValue;
    private HashSet<String> enumValues = null;

    public ServerVariable(String description, String defaultValue, HashSet<String> enumValues) {
        this.description = description;
        this.defaultValue = defaultValue;
        this.enumValues = enumValues;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public HashSet<String> getEnumValues() {
        return enumValues;
    }
}
