package org.jboss.fuse.tnb.common.config;

public class SpringBootConfiguration extends Configuration {
    public static final String CAMEL_SPRINGBOOT_VERSION = "camel.springboot.version";
    public static final String SPRINGBOOT_VERSION = "springboot.version";

    public static final String SPRINGBOOT_PLATFORM_GROUP_ID = "springboot.platform.group.id";
    public static final String SPRINGBOOT_PLATFORM_ARTIFACT_ID = "springboot.platform.artifact.id";

    public static final String CAMEL_PLATFORM_GROUP_ID = "camel.springboot.platform.group.id";
    public static final String CAMEL_PLATFORM_ARTIFACT_ID = "camel.springboot.platform.artifact.id";

    public static final String CAMEL_SPRINGBOOT_ARCHETYPE_GROUP_ID = "camel.springboot.archetype.group.id";
    public static final String CAMEL_SPRINGBOOT_ARCHETYPE_ARTIFACT_ID = "camel.springboot.archetype.artifact.id";
    public static final String CAMEL_SPRINGBOOT_ARCHETYPE_VERSION = "camel.springboot.archetype.version";

    public static String springBootVersion() {
        return getProperty(SPRINGBOOT_VERSION, "2.6.1");
    }

    public static String camelSpringBootVersion() {
        return getProperty(CAMEL_SPRINGBOOT_VERSION, "3.14.0");
    }

    public static String springBootPlatformGroupId() {
        return getProperty(SPRINGBOOT_PLATFORM_GROUP_ID, "org.springframework.boot");
    }

    public static String springbootPlatformArtifactId() {
        return getProperty(SPRINGBOOT_PLATFORM_ARTIFACT_ID, "spring-boot-dependencies");
    }

    public static String camelPlatformGroupId() {
        return getProperty(CAMEL_PLATFORM_GROUP_ID, "org.apache.camel.springboot");
    }

    public static String camelPlatformArtifactId() {
        return getProperty(CAMEL_PLATFORM_ARTIFACT_ID, "camel-spring-boot-dependencies");
    }

    public static String camelSpringBootArchetypeGroupId() {
        return getProperty(CAMEL_SPRINGBOOT_ARCHETYPE_GROUP_ID, "org.apache.camel.archetypes");
    }

    public static String camelSpringBootArchetypeArtifactId() {
        return getProperty(CAMEL_SPRINGBOOT_ARCHETYPE_ARTIFACT_ID, "camel3-archetype-spring-boot");
    }

    public static String camelSpringBootArchetypeVersion() {
        return getProperty(CAMEL_SPRINGBOOT_ARCHETYPE_VERSION, "1.0.2");
    }
}
