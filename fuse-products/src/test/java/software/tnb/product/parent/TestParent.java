package software.tnb.product.parent;

import static org.assertj.core.api.Assertions.assertThat;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.product.ProductType;
import software.tnb.product.cq.configuration.QuarkusConfiguration;
import software.tnb.product.integration.builder.IntegrationBuilder;
import software.tnb.product.routebuilder.DummyRouteBuilder;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import org.apache.maven.model.Dependency;

/**
 * Global test parent.
 *
 * Contains useful methods for all tests.
 */
public class TestParent {
    protected static void setProduct(ProductType type) {
        System.setProperty(TestConfiguration.PRODUCT, type.getValue());
    }

    @BeforeAll
    public static void beforeAllParent() {
        System.setProperty(QuarkusConfiguration.QUARKUS_VERSION, QuarkusConfiguration.DEFAULT_QUARKUS_VERSION);
        System.setProperty(QuarkusConfiguration.QUARKUS_PLATFORM_GROUP_ID,  QuarkusConfiguration.DEFAULT_QUARKUS_PLATFORM_GROUP_ID);
        System.setProperty(QuarkusConfiguration.QUARKUS_PLATFORM_ARTIFACT_ID,  QuarkusConfiguration.DEFAULT_QUARKUS_PLATFORM_ARTIFACT_ID);
        System.setProperty(QuarkusConfiguration.QUARKUS_PLATFORM_VERSION,  QuarkusConfiguration.DEFAULT_QUARKUS_VERSION);
        System.setProperty(QuarkusConfiguration.CAMEL_QUARKUS_VERSION, QuarkusConfiguration.DEFAULT_CAMEL_QUARKUS_VERSION);
        System.setProperty(QuarkusConfiguration.CAMEL_QUARKUS_PLATFORM_GROUP_ID,  QuarkusConfiguration.DEFAULT_CAMEL_QUARKUS_PLATFORM_GROUP_ID);
        System.setProperty(QuarkusConfiguration.CAMEL_QUARKUS_PLATFORM_ARTIFACT_ID,  QuarkusConfiguration.DEFAULT_CAMEL_QUARKUS_PLATFORM_ARTIFACT_ID);
        System.setProperty(QuarkusConfiguration.CAMEL_QUARKUS_PLATFORM_VERSION,  QuarkusConfiguration.DEFAULT_CAMEL_QUARKUS_VERSION);

    }

    @AfterAll
    public static void afterAll() {
        System.clearProperty(TestConfiguration.PRODUCT);
    }

    @BeforeEach
    public void printTest(TestInfo info) {
        System.out.println("Running " + info.getDisplayName());
    }

    protected static String name() {
        return "test";
    }

    protected IntegrationBuilder dummyIb() {
        return new IntegrationBuilder(name()).fromRouteBuilder(new DummyRouteBuilder());
    }

    protected void verifyDependency(Dependency d, String groupId, String artifactId, String version) {
        assertThat(d.getGroupId()).isEqualTo(groupId);
        assertThat(d.getArtifactId()).isEqualTo(artifactId);
        assertThat(d.getVersion()).isEqualTo(version);
    }

}
