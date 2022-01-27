package org.jboss.fuse.tnb.customizer.datasource;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.product.ck.generated.IntegrationSpec;
import org.jboss.fuse.tnb.product.integration.Customizer;
import org.jboss.fuse.tnb.product.integration.IntegrationSpecCustomizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DataSourceCustomizer extends Customizer implements IntegrationSpecCustomizer {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceCustomizer.class);

    protected String type;
    protected String url;
    protected String username;
    protected String password;
    protected String driver;

    public DataSourceCustomizer(String type, String url, String username, String password, String driver) {
        this.type = type;
        this.url = url;
        this.username = username;
        this.password = password;
        this.driver = driver;
    }

    @Override
    public void customize() {
        switch (TestConfiguration.product()) {
            case CAMEL_K:
            case CAMEL_QUARKUS:
                customizeQuarkus();
                break;
            case CAMEL_SPRINGBOOT:
                customizeSpringboot();
                break;
            default:
                throw new UnsupportedOperationException("Customizer is not supported for selected product!");
        }
    }

    private void customizeSpringboot() {
        final Properties appProperties = getApplicationProperties();

        appProperties.putAll(
            Map.of("spring.datasource.url", url,
                "spring.datasource.username", username,
                "spring.datasource.password", password,
                "spring.datasource.driver-class-name", driver)
        );

        getIntegrationBuilder().addToProperties(appProperties);

        final String[] dbDependencies = getDbAllocatorDependencies();
        final List<String> dependencies = new LinkedList<>();
        dependencies.addAll(Arrays.asList(dbDependencies));
        dependencies.add("org.springframework.boot:spring-boot-starter-jdbc");
        if (dbDependencies.length == 0 && "postgresql".equals(type)) {
            dependencies.add("org.postgresql:postgresql");
        }
        getIntegrationBuilder().dependencies(dependencies.toArray(new String[0]));
    }

    private void customizeQuarkus() {
        final Properties appProperties = getApplicationProperties();
        appProperties.put("quarkus.datasource.db-kind", type);
        appProperties.put("quarkus.datasource.jdbc.url", url);
        appProperties.put("quarkus.datasource.username", username);
        appProperties.put("quarkus.datasource.password", password);

        final String[] dbDependencies = getDbAllocatorDependencies();

        if (dbDependencies.length == 0) {
            getIntegrationBuilder().dependencies("io.quarkus:quarkus-jdbc-" + type);
        } else {
            getIntegrationBuilder().dependencies(dbDependencies);
        }
    }

    @Override
    public void customizeIntegration(IntegrationSpec integrationSpec) {
        final IntegrationSpec.TraitConfig traitConfig = new IntegrationSpec.TraitConfig("properties",
            Collections.singletonList("quarkus.datasource.db-kind=" + type)
        );
        if (integrationSpec.getTraits() == null) {
            integrationSpec.setTraits(new HashMap<>());
        }
        integrationSpec.getTraits().put("builder", traitConfig);
    }

    private String[] getDbAllocatorDependencies() {
        try {
            final Class configClass = Class.forName("org.jboss.fuse.tnb.dballocator.service.DbAllocatorConfiguration");
            final Method method = configClass.getDeclaredMethod("getDependencies");
            return (String[]) method.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            LOG.info("DbAllocatorConfiguration class is not present.");
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
        }
        return new String[0];
    }
}
