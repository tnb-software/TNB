package org.jboss.fuse.tnb.product.cq;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.utils.MapUtils;
import org.jboss.fuse.tnb.product.integration.IntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.IntegrationGenerator;
import org.jboss.fuse.tnb.product.util.Maven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public interface Quarkus {

    /**
     * Creates a camel-quarkus app using the quarkus-maven-plugin.
     * @param name name of the application
     * @param routeDefinition routebuilder class that will be placed into the app
     * @param camelComponents necessary camel components
     */
    default void createApp(String name, IntegrationBuilder integrationBuilder, String... camelComponents) {
        Logger LOG = LoggerFactory.getLogger(Quarkus.class);
        LOG.info("Creating application project for integration {}", name);

        String extensions = Arrays.stream(camelComponents).map(c -> "camel-quarkus-" + c).collect(Collectors.joining(","));
        if (OpenshiftConfiguration.isOpenshift()) {
            extensions += ",openshift";
        }
        Maven.invoke(
            TestConfiguration.appLocation(),
            Collections.singletonList("io.quarkus:quarkus-maven-plugin:" + TestConfiguration.quarkusVersion() + ":create"),
            MapUtils.toProperties(Map.of(
                "projectGroupId", TestConfiguration.appGroupId(),
                "projectArtifactId", name,
                "extensions", extensions
            ))
        );

        IntegrationGenerator.toFile(integrationBuilder, TestConfiguration.appLocation().resolve(name));
    }
}
