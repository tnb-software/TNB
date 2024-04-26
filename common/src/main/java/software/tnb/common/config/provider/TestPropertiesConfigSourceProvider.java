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
import java.util.LinkedHashMap;
import java.util.Map;

@AutoService(ConfigSourceProvider.class)
public class TestPropertiesConfigSourceProvider implements ConfigSourceProvider {
    private static final Logger LOG = LoggerFactory.getLogger(TestPropertiesConfigSourceProvider.class);
    private static final String TEST_PROPERTIES = System.getProperty("test.properties", "test.properties");

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader classLoader) {
        try {
            if (System.getProperty(TEST_PROPERTIES) == null) {
                LOG.trace("No custom properties file defined, using ./test.properties file if it exists");
            }
            File testProperties = new File(TEST_PROPERTIES);
            if (!testProperties.exists()) {
                if (TEST_PROPERTIES == "test.properties") {
                    LOG.trace("Default properties file ./test.properties does not exist!");
                } else {
                    LOG.error("Test properties file {} defined in {} system property does not exist!",
                        System.getProperty(TEST_PROPERTIES), TEST_PROPERTIES);
                    LOG.error(">>>TEST CONTINUES WITHOUT LOADING PROPERTIES FROM SPECIFIED FILE<<<");
                    //It would be best to throw an FNF exception at this point - but it's not possible because of microprofile impl
                }
                return Collections.emptyList();
            }
            LOG.info("Loading properties from " + testProperties.getAbsolutePath());
            PropertyFileConfigSource propertiesSource = new PropertyFileConfigSource(testProperties.toURI().toURL());
            propertiesSource.getProperties()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(
                    LinkedHashMap::new,
                    (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                    LinkedHashMap::putAll
                )
                .forEach((key, value) -> LOG.info(key + "=" + value));
            return Collections.singletonList(propertiesSource);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
