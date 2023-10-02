package software.tnb.product.customizer.component.datasource;

import software.tnb.product.ck.customizer.IntegrationSpecCustomizer;
import software.tnb.product.customizer.ProductsCustomizer;

import org.apache.camel.v1.IntegrationSpec;
import org.apache.camel.v1.integrationspec.Traits;
import org.apache.camel.v1.integrationspec.traits.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DataSourceCustomizer extends ProductsCustomizer implements IntegrationSpecCustomizer {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceCustomizer.class);

    protected String type;
    protected String url;
    protected String username;
    protected String password;
    protected String driver;

    public DataSourceCustomizer(DatabaseType type, String url, String username, String password) {
        this(type.name().toLowerCase(), url, username, password, type.driverClass());
    }

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
        final String[] dbDependencies = getDbAllocatorDependencies();
        final List<String> dependencies = new LinkedList<>(Arrays.asList(dbDependencies));
        dependencies.add("org.springframework.boot:spring-boot-starter-jdbc");
        if (dbDependencies.length == 0) {
            if ("postgresql".equals(type)) {
                dependencies.add("org.postgresql:postgresql");
            } else if (type.contains("oracle")) {
                dependencies.add("com.oracle.database.jdbc:ojdbc11");
            } else if (type.contains("mssql")) {
                dependencies.add("com.microsoft.sqlserver:mssql-jdbc");

                // Spring Boot 2.7 has upgrade the MSSQL driver from v9 to v10.
                // The updated driver now enables encryption by default which may break existing applications
                // The recommended advice is to either install a trusted certificate or update your JDBC connection URL to include encrypt=false
                // https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.7-Release-Notes#microsoft-sql-server-jdbc-drive-10
                url = url + ";encrypt=false;";
            } else if (type.contains("mysql")) {
                dependencies.add("com.mysql:mysql-connector-j");
            }  else if (type.contains("mariadb")) {
                dependencies.add("org.mariadb.jdbc:mariadb-java-client");
            } else if (type.contains("db2")) {
                dependencies.add("com.ibm.db2:jcc");
            }
        }

        getIntegrationBuilder().addToProperties(
            Map.of("spring.datasource.url", url,
                "spring.datasource.username", username,
                "spring.datasource.password", password,
                "spring.datasource.driver-class-name", driver)
        );

        getIntegrationBuilder().dependencies(dependencies.toArray(new String[0]));
    }

    @Override
    public void customizeQuarkus() {
        if (type.contains("mssql")) {
            url = url + ";encrypt=false;"; //turn off SSL similarly to springboot (method above)
        }
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
    public void customizeIntegration(IntegrationSpec integrationSpec) {
        final Traits traits = integrationSpec.getTraits() == null ? new Traits() : integrationSpec.getTraits();
        final Builder builder = traits.getBuilder() == null ? new Builder() : traits.getBuilder();
        final List<String> properties = builder.getProperties() == null ? new ArrayList<>() : builder.getProperties();
        properties.add("quarkus.datasource.db-kind=" + type);
        builder.setProperties(properties);
        traits.setBuilder(builder);
        integrationSpec.setTraits(traits);
    }

    private String[] getDbAllocatorDependencies() {
        try {
            final Class configClass = Class.forName("software.tnb.dballocator.service.DbAllocatorConfiguration");
            final Method method = configClass.getDeclaredMethod("getDependencies");
            return (String[]) method.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            LOG.info("DbAllocatorConfiguration class is not present.");
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
        }
        return new String[0];
    }

    public enum DatabaseType {
        MARIADB("org.mariadb.jdbc.Driver"),
        MYSQL("com.mysql.jdbc.Driver"),
        POSTGRESQL("org.postgresql.Driver"),
        MSSQL("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        private final String driverClass;

        DatabaseType(String driverClass) {
            this.driverClass = driverClass;
        }

        public String driverClass() {
            return driverClass;
        }
    }
}
