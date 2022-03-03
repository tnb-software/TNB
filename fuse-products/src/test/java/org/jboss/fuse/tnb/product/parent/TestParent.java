package org.jboss.fuse.tnb.product.parent;

import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.integration.builder.IntegrationBuilder;
import org.jboss.fuse.tnb.product.routebuilder.DummyRouteBuilder;

import org.junit.jupiter.api.AfterAll;
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
