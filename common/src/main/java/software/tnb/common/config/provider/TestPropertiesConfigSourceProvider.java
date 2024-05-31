package software.tnb.common.config.provider;

import org.apache.geronimo.config.configsource.PropertyFileConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collections;

@AutoService(ConfigSourceProvider.class)
public class TestPropertiesConfigSourceProvider implements ConfigSourceProvider {
    private static final Logger LOG = LoggerFactory.getLogger(TestPropertiesConfigSourceProvider.class);
    private static final String TEST_PROPERTIES = "test.properties";

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader classLoader) {
        try {
            if (System.getProperty(TEST_PROPERTIES) == null) {
                LOG.trace("No custom properties file defined, using test.properties file if it exists");
                return Collections.emptyList();
            }
            File testProperties = new File(System.getProperty(TEST_PROPERTIES));
            if (!testProperties.exists()) {
                LOG.warn("Test properties file {} defined in {} system property does not exist!",
                    System.getProperty(TEST_PROPERTIES), TEST_PROPERTIES);
                return Collections.emptyList();
            }
            LOG.info("Loading properties from {}", testProperties.getAbsolutePath());
            PropertyFileConfigSource propertiesSource = new PropertyFileConfigSource(testProperties.toURI().toURL());
            propertiesSource.getProperties().forEach((key, value) -> LOG.trace("  {}={}", key, value));
            return Collections.singletonList(propertiesSource);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
