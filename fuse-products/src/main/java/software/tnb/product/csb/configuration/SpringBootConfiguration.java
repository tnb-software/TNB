package software.tnb.product.csb.configuration;

import software.tnb.common.utils.VersionUtils;
import software.tnb.product.camel.CamelConfiguration;

import org.apache.commons.lang3.StringUtils;

public class SpringBootConfiguration extends CamelConfiguration {
    public static final String CAMEL_SPRINGBOOT_VERSION = "camel.springboot.version";
    public static final String SPRINGBOOT_VERSION = "springboot.version";

    public static final String SPRINGBOOT_PLATFORM_GROUP_ID = "springboot.platform.group.id";
    public static final String SPRINGBOOT_PLATFORM_ARTIFACT_ID = "springboot.platform.artifact.id";

    public static final String CAMEL_PLATFORM_GROUP_ID = "camel.springboot.platform.group.id";
    public static final String CAMEL_PLATFORM_ARTIFACT_ID = "camel.springboot.platform.artifact.id";

    public static final String CAMEL_SPRINGBOOT_ARCHETYPE_GROUP_ID = "camel.springboot.archetype.group.id";
    public static final String CAMEL_SPRINGBOOT_ARCHETYPE_ARTIFACT_ID = "camel.springboot.archetype.artifact.id";
    public static final String CAMEL_SPRINGBOOT_ARCHETYPE_VERSION = "camel.springboot.archetype.version";

    public static final String CAMEL_SPRINGBOOT_EXAMPLES_REPO = "camel.springboot.examples.repo";
    public static final String CAMEL_SPRINGBOOT_EXAMPLES_BRANCH = "camel.springboot.examples.branch";

    public static final String OPENSHIFT_MAVEN_PLUGIN_GROUP_ID = "openshift-maven-plugin-group-id";
    public static final String OPENSHIFT_MAVEN_PLUGIN_VERSION = "openshift-maven-plugin-version";

    public static final String MAVEN_COMPILER_PLUGIN_VERSION = "maven-compiler-plugin-version";

    public static final String OPENSHIFT_SB_BASE_IMAGE = "openshift-sb-base-image";

    public static String springBootVersion() {
        return getProperty(SPRINGBOOT_VERSION, "2.6.1");
    }

    public static String camelSpringBootVersion() {
        return getProperty(CAMEL_SPRINGBOOT_VERSION, "3.14.0");
    }

    public static String getCamelSpringbootExamplesRepo() {
        return getProperty(CAMEL_SPRINGBOOT_EXAMPLES_REPO, "https://github.com/apache/camel-spring-boot-examples");
    }

    public static String camelSpringBootExamplesBranch() {
        return getProperty(CAMEL_SPRINGBOOT_EXAMPLES_BRANCH, camelSpringBootVersion());
    }

    public static String springBootPlatformGroupId() {
        return getProperty(SPRINGBOOT_PLATFORM_GROUP_ID, "org.springframework.boot");
    }

    public static String springBootPlatformArtifactId() {
        return getProperty(SPRINGBOOT_PLATFORM_ARTIFACT_ID, "spring-boot-dependencies");
    }

    public static String camelPlatformGroupId() {
        return getProperty(CAMEL_PLATFORM_GROUP_ID, "org.apache.camel.springboot");
    }

    public static String camelPlatformArtifactId() {
        return getProperty(CAMEL_PLATFORM_ARTIFACT_ID, "camel-spring-boot-dependencies");
    }

    public static String camelSpringBootArchetypeGroupId() {
        return getProperty(CAMEL_SPRINGBOOT_ARCHETYPE_GROUP_ID, "software.tnb");
    }

    public static String camelSpringBootArchetypeArtifactId() {
        return getProperty(CAMEL_SPRINGBOOT_ARCHETYPE_ARTIFACT_ID, "camel3-archetype-spring-boot");
    }

    public static String camelSpringBootArchetypeVersion() {
        return getProperty(CAMEL_SPRINGBOOT_ARCHETYPE_VERSION, "0.1.8");
    }

    public static String openshiftMavenPluginGroupId() {
        return getProperty(OPENSHIFT_MAVEN_PLUGIN_GROUP_ID, "org.eclipse.jkube");
    }

    public static String openshiftMavenPluginVersion() {
        return getProperty(OPENSHIFT_MAVEN_PLUGIN_VERSION, () -> StringUtils.removeStart(VersionUtils.getInstance()
            .getLatestGitHubReleaseTag("eclipse/jkube", "1.9.1"), "v"));
    }

    public static String mavenCompilerPluginVersion() {
        return getProperty(MAVEN_COMPILER_PLUGIN_VERSION, "3.8.1");
    }

    public static String openshiftBaseImage() {
        return getProperty(OPENSHIFT_SB_BASE_IMAGE, "registry.access.redhat.com/ubi8/openjdk-11:latest");
    }
}
