package org.jboss.fuse.tnb.product.cq;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.utils.MapUtils;
import org.jboss.fuse.tnb.product.Product;
import org.jboss.fuse.tnb.product.util.Maven;
import org.jboss.fuse.tnb.product.util.RouteBuilderGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.javapoet.CodeBlock;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Quarkus extends Product {
    private static final Logger log = LoggerFactory.getLogger(Quarkus.class);
    public void createApp(String name, CodeBlock routeDefinition, String... camelComponents) {
        log.info("Creating application project for integration {}", name);

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

        RouteBuilderGenerator.toFile(routeDefinition, TestConfiguration.appLocation().resolve(name).resolve("src/main/java"));
    }
}
