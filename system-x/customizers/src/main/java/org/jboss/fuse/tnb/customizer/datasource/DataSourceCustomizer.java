package org.jboss.fuse.tnb.customizer.datasource;

import org.jboss.fuse.tnb.product.ck.customizer.IntegrationSpecCustomizer;
import org.jboss.fuse.tnb.product.customizer.ProductsCustomizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.fabric8.camelk.v1.IntegrationSpecBuilder;

public class DataSourceCustomizer extends ProductsCustomizer implements IntegrationSpecCustomizer {

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
    public void customizeCamelK() {
        customizeQuarkus();
    }

    @Override
    public void customizeSpringboot() {
        getIntegrationBuilder().addToProperties(
            Map.of("spring.datasource.url", url,
                "spring.datasource.username", username,
                "spring.datasource.password", password,
                "spring.datasource.driver-class-name", driver)
        );

        final String[] dbDependencies = getDbAllocatorDependencies();
        final List<String> dependencies = new LinkedList<>(Arrays.asList(dbDependencies));
        dependencies.add("org.springframework.boot:spring-boot-starter-jdbc");
        if (dbDependencies.length == 0 && "postgresql".equals(type)) {
            dependencies.add("org.postgresql:postgresql");
        }
        getIntegrationBuilder().dependencies(dependencies.toArray(new String[0]));
    }

    @Override
    public void customizeQuarkus() {
        getIntegrationBuilder().addToProperties(
            Map.of(
                "quarkus.datasource.db-kind", type,
                "quarkus.datasource.jdbc.url", url,
                "quarkus.datasource.username", username,
                "quarkus.datasource.password", password
                )
        );

        final String[] dbDependencies = getDbAllocatorDependencies();

        if (dbDependencies.length == 0) {
            getIntegrationBuilder().dependencies("io.quarkus:quarkus-jdbc-" + type);
        } else {
            getIntegrationBuilder().dependencies(dbDependencies);
        }
    }

    @Override
    public void customizeIntegration(IntegrationSpecBuilder integrationSpecBuilder) {
        Map<String, Object> configuration = Map.of("properties", List.of("quarkus.datasource.db-kind=" + type));
        mergeTraitConfiguration(integrationSpecBuilder, "builder", configuration);
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
