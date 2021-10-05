package org.jboss.fuse.tnb.common.config.provider;

import org.apache.geronimo.config.configsource.PropertyFileConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collections;

public class TestPropertiesConfigSourceProvider implements ConfigSourceProvider {
    private static final Logger LOG = LoggerFactory.getLogger(TestPropertiesConfigSourceProvider.class);
    private static final String TEST_PROPERTIES = "test.properties";

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader classLoader) {
        try {
            if (System.getProperty(TEST_PROPERTIES) == null) {
                return Collections.emptyList();
            }
            File testProperties = new File(System.getProperty(TEST_PROPERTIES));
            if (!testProperties.exists()) {
                LOG.warn("Test properties file {} defined in {} system property does not exist!",
                    System.getProperty(TEST_PROPERTIES), TEST_PROPERTIES);
                return Collections.emptyList();
            }
            return Collections.singletonList(new PropertyFileConfigSource(testProperties.toURI().toURL()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
