package software.tnb.common.utils;

import org.apache.commons.io.FileUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public final class PropertiesUtils {
    private PropertiesUtils() {
    }

    public static Properties toCamelCaseProperties(Properties underscoreProperties) {
        Properties camelCaseProperties = new Properties();
        underscoreProperties.forEach((key, value) -> camelCaseProperties.put(StringUtils.replaceUnderscoreWithCamelCase(key.toString()), value));
        return camelCaseProperties;
    }

    /**
     * Dumps the properties as new-line separated string of key=value.
     *
     * @param properties properties
     * @return string
     */
    public static String toString(Properties properties) {
        return toString(properties, "");
    }

    public static String toString(Properties properties, String prefix) {
        return properties.entrySet().stream().map(entry -> prefix + entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Converts properties object into a map.
     *
     * @param properties of service, obtained from credentials.yaml
     * @param prefix e.g. "camel.kamelet.aws-s3-source."
     * @return map with keys in camelCase
     */
    public static Map<String, String> toMap(Properties properties, String prefix) {
        Map<String, String> map = new HashMap<>();
        if (properties != null) {
            properties.forEach((key, value) -> map.put(prefix + StringUtils.replaceUnderscoreWithCamelCase(key.toString()), value.toString()));
        }
        return map;
    }

    public static Map<String, String> toMap(Properties properties) {
        return toMap(properties, "");
    }

    public static String toUriParameters(Properties properties) {
        return toString(properties).replaceAll("\n", "&");
    }

    public static Properties fromFile(String filePath) {
        Properties properties = new Properties();
        try (InputStream is = new FileInputStream(filePath)) {
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties from " + filePath, e);
        }
        return properties;
    }

    public static void setProperties(Path filePath, Map<String, String> props) {
        final Properties properties = new Properties();
        try (InputStream is = new FileInputStream(filePath.toFile())) {
            properties.load(is);
            props.forEach(properties::setProperty);
            //recreate file
            FileUtils.deleteQuietly(filePath.toFile());
            IOUtils.writeFile(filePath, toString(properties));
        } catch (IOException e) {
            throw new RuntimeException("Unable to load properties from " + filePath, e);
        }
    }
}
