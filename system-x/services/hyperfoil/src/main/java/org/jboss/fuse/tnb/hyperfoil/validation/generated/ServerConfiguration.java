package org.jboss.fuse.tnb.hyperfoil.validation.generated;

import java.util.Map;

/**
 * Representing a Server configuration.
 */
public class ServerConfiguration {
    private String url;
    private String description;
    private Map<String, ServerVariable> variables;

    public ServerConfiguration(String url, String description, Map<String, ServerVariable> variables) {
        this.url = url;
        this.description = description;
        this.variables = variables;
    }

    /**
     * Format URL template using given variables.
     *
     * @param variables A map between a variable name and its value.
     * @return Formatted URL.
     */
    public String url(Map<String, String> variables) {
        String urlInner = this.url;

        // go through variables and replace placeholders
        for (Map.Entry<String, ServerVariable> variable: this.variables.entrySet()) {
            String name = variable.getKey();
            ServerVariable serverVariable = variable.getValue();
            String value = serverVariable.getDefaultValue();

            if (variables != null && variables.containsKey(name)) {
                value = variables.get(name);
                if (serverVariable.getEnumValues().size() > 0 && !serverVariable.getEnumValues().contains(value)) {
                    throw new RuntimeException("The variable " + name + " in the server URL has invalid value " + value + ".");
                }
            }
            urlInner = urlInner.replaceAll("\\{" + name + "\\}", value);
        }
        return urlInner;
    }

    /**
     * Format URL template using default server variables.
     *
     * @return Formatted URL.
     */
    public String getURL() {
        return url(null);
    }
}
