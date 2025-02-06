package software.tnb.common.config.source;

import software.tnb.common.utils.PropertiesUtils;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

/**
 * Defines a ConfigSource that loads the properties from a properties file defined in "test.properties" system property.
 * <p/>
 * This ConfigSource has the highest priority of all config sources (ordinal 500), that means that the values in test.properties take precedence
 * over environment and system properties defined in any way.
 * <p/>
 * Note for the future: If we ever wanted to achieve a hierarchy like this: values in pom.xml -> test.properties -> system properties, then two
 * things need to happen:
 * - pass the properties from pom.xml as environment variables, not as system properties
 * - lower the ordinal of this class to a number between 300 and 400
 */
@AutoService(ConfigSource.class)
public class TestPropertiesConfigSource implements ConfigSource {
    private static final Logger LOG = LoggerFactory.getLogger(TestPropertiesConfigSource.class);
    private static final String TEST_PROPERTIES = "test.properties";

    private final Map<String, String> properties = new TreeMap<>();

    public TestPropertiesConfigSource() {
        File testProperties = new File(System.getProperty(TEST_PROPERTIES, "test.properties"));
        if (!testProperties.exists()) {
            LOG.warn("Properties file '{}' defined in '{}' system property does not exist!", System.getProperty(TEST_PROPERTIES), TEST_PROPERTIES);
            return;
        }

        LOG.info("Loading properties from {}", testProperties.getAbsolutePath());
        properties.putAll(PropertiesUtils.toMap(PropertiesUtils.fromFile(testProperties.getAbsolutePath())));
        properties.forEach((key, value) -> LOG.trace("  {}={}", key, value));
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public int getOrdinal() {
        return 500;
    }

    @Override
    public String getValue(String s) {
        return getProperties().get(s);
    }

    @Override
    public String getName() {
        return "TestPropertiesConfigSource";
    }
}
