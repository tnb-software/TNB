package org.jboss.fuse.tnb.product.app;

import static org.junit.jupiter.api.Assertions.fail;

import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.config.SpringBootConfiguration;
import org.jboss.fuse.tnb.common.config.TestConfiguration;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.csb.application.LocalSpringBootApp;
import org.jboss.fuse.tnb.product.csb.integration.builder.SpringBootIntegrationBuilder;
import org.jboss.fuse.tnb.product.integration.Resource;
import org.jboss.fuse.tnb.product.util.maven.Maven;
import org.jboss.fuse.tnb.product.util.maven.handler.MavenFileOutputHandler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.apache.maven.shared.invoker.InvocationRequest;
import org.assertj.core.api.SoftAssertions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Tag("unit")
public class SpringBootAppTest extends LocalAppTestParent {
    @Override
    public ProductType product() {
        return ProductType.CAMEL_SPRINGBOOT;
    }

    @AfterEach
    public void clearProperties() {
        List.of(OpenshiftConfiguration.USE_OPENSHIFT).forEach(System::clearProperty);
    }

    @Test
    public void shouldCreateSpringBootAppTest() {
        LocalSpringBootApp app = new LocalSpringBootApp(dummyIb());

        assertThat(app.getName()).isEqualTo(name());
        assertThat(TEST_INVOKER.getRequests()).hasSize(2);

        SoftAssertions sa = new SoftAssertions();
        InvocationRequest request = TEST_INVOKER.getRequests().get(0);
        sa.assertThat(request.getBaseDirectory().getAbsolutePath()).isEqualTo(TestConfiguration.appLocation().toAbsolutePath().toString());
        sa.assertThat(request.getGoals()).containsOnly("archetype:generate");
        sa.assertThat(request.getProperties()).isEqualTo(Map.of(
            "archetypeGroupId", SpringBootConfiguration.camelSpringBootArchetypeGroupId(),
            "archetypeArtifactId", SpringBootConfiguration.camelSpringBootArchetypeArtifactId(),
            "archetypeVersion", SpringBootConfiguration.camelSpringBootArchetypeVersion(),
            "groupId", TestConfiguration.appGroupId(),
            "artifactId", name(),
            "version", "1.0.0-SNAPSHOT",
            "package", TestConfiguration.appGroupId(),
            "maven-compiler-plugin-version", SpringBootConfiguration.mavenCompilerPluginVersion(),
            "spring-boot-version", SpringBootConfiguration.springBootVersion(),
            "camel-version", SpringBootConfiguration.camelSpringBootVersion()
        ));
        sa.assertThat(request.getOutputHandler(null)).isInstanceOf(MavenFileOutputHandler.class);

        request = TEST_INVOKER.getRequests().get(1);
        sa.assertThat(request.getBaseDirectory().getAbsolutePath()).isEqualTo(APP_PATH.toAbsolutePath().toString());
        sa.assertThat(request.getGoals()).containsOnly("clean", "package");
        sa.assertThat(request.getProperties()).isEqualTo(Map.of(
            "skipTests", "true"
        ));
        sa.assertThat(request.getOutputHandler(null)).isInstanceOf(MavenFileOutputHandler.class);

        sa.assertAll();
    }

    @Test
    public void shouldCreateAppFromGitRepositoryTest() {
        final String repository = "https://github.com/jboss-fuse/camel-spring-boot-examples.git";
        final Path expectedPath = TestConfiguration.appLocation().resolve("camel-spring-boot-examples");
        new LocalSpringBootApp(new SpringBootIntegrationBuilder(name()).fromGitRepository(repository));

        assertThat(expectedPath).exists();
        assertThat(expectedPath).isNotEmptyDirectory();
    }

    @Test
    public void shouldUseSpringXMLTest() {
        TEST_INVOKER.mockExecution(() -> {
            try {
                Files.copy(this.getClass().getResourceAsStream("/org/jboss/fuse/tnb/product/app/MySpringBootApplication.txt"),
                    TestConfiguration.appLocation().resolve(name()).resolve("src/main/java")
                        .resolve(TestConfiguration.appGroupId().replaceAll("\\.", "/")).resolve("MySpringBootApplication.java")
                );
            } catch (Exception e) {
                fail("Unable to copy file directories", e);
            }
        });

        SpringBootIntegrationBuilder ib = new SpringBootIntegrationBuilder(name())
            .fromSpringBootXmlCamelContext("org/jboss/fuse/tnb/product/camel-context.xml");
        new LocalSpringBootApp(ib);

        assertThat(ib.getXmlCamelContext()).hasSize(1);
        Resource xml = ib.getXmlCamelContext().get(0);
        assertThat(APP_PATH.resolve("src/main/resources").resolve(xml.getName())).exists();
        assertThat(APP_PATH.resolve("src/main/resources").resolve(xml.getName())).content().isEqualTo(xml.getContent());

        assertThat(APP_PATH.resolve("src/main/java").resolve(PACKAGE_DIRECTORIES).resolve("MySpringBootApplication.java")).exists();
        assertThat(APP_PATH.resolve("src/main/java").resolve(PACKAGE_DIRECTORIES).resolve("MySpringBootApplication.java")).content()
            .contains("@ImportResource(value = {\"classpath:" + xml.getName() + "\"})");

        assertThat(Maven.loadPom(POM_PATH.toFile()).getDependencies()).anyMatch(d -> "org.apache.camel.springboot".equals(d.getGroupId())
            && "camel-spring-boot-xml-starter".equals(d.getArtifactId()));
    }

    @Test
    public void shouldAddDependenciesTest() {
        final String groupId = "com.test";
        final String artifactId = "example";
        final String version = "1.0";
        new LocalSpringBootApp(dummyIb().dependencies(groupId + ":" + artifactId + ":" + version));
        verifyDependencies(groupId, artifactId, version);
    }
}
