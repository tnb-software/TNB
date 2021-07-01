package org.jboss.fuse.tnb.common.config.provider;

import org.apache.geronimo.config.configsource.PropertyFileConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;

public class TestPropertiesConfigSourceProvider implements ConfigSourceProvider {

    private static final String TEST_PROPERTIES = "test.properties";

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader classLoader) {
        try {
            URL file = Paths.get(System.getProperty(TEST_PROPERTIES, "test.properties")).toUri().toURL();
            PropertyFileConfigSource propertyFileConfigSource = new PropertyFileConfigSource(file);
            return Collections.singletonList(propertyFileConfigSource);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
