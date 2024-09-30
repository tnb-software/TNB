package software.tnb.product.app;

import static org.assertj.core.api.Assertions.assertThat;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.config.TestConfiguration;
import software.tnb.common.product.ProductType;
import software.tnb.product.cq.application.LocalQuarkusApp;
import software.tnb.product.cq.configuration.QuarkusConfiguration;
import software.tnb.product.util.maven.Maven;
import software.tnb.product.util.maven.handler.MavenFileOutputHandler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag("unit")
public class QuarkusAppTest extends LocalAppTestParent {
    @Override
    public ProductType product() {
        return ProductType.CAMEL_QUARKUS;
    }

    @AfterEach
    public void clearProperties() {
        List.of(QuarkusConfiguration.QUARKUS_NATIVE_BUILD, QuarkusConfiguration.CAMEL_QUARKUS_VERSION, OpenshiftConfiguration.USE_OPENSHIFT)
            .forEach(System::clearProperty);
    }

    @Test
    public void shouldCreateQuarkusAppTest() {
        LocalQuarkusApp app = new LocalQuarkusApp(dummyIb());

        assertThat(app.getName()).isEqualTo(name());
        Assertions.assertThat(TEST_INVOKER.getRequests()).hasSize(2);

        SoftAssertions sa = new SoftAssertions();
        InvocationRequest request = TEST_INVOKER.getRequests().get(0);
        sa.assertThat(request.getBaseDirectory().getAbsolutePath()).isEqualTo(TestConfiguration.appLocation().toAbsolutePath().toString());
        sa.assertThat(request.getGoals()).hasSize(1);
        sa.assertThat(request.getGoals().get(0)).isEqualTo(String.format("%s:%s:%s:create",
            QuarkusConfiguration.quarkusPlatformGroupId(), "quarkus-maven-plugin", QuarkusConfiguration.quarkusPlatformVersion()));
        sa.assertThat(request.getProperties()).isEqualTo(Map.of(
            "projectGroupId", TestConfiguration.appGroupId(),
            "projectVersion", TestConfiguration.appVersion(),
            "projectArtifactId", name(),
            "platformGroupId", QuarkusConfiguration.quarkusPlatformGroupId(),
            "platformArtifactId", QuarkusConfiguration.quarkusPlatformArtifactId(),
            "platformVersion", QuarkusConfiguration.quarkusPlatformVersion(),
            "extensions", ""
        ));
        sa.assertThat(request.getOutputHandler(null)).isInstanceOf(MavenFileOutputHandler.class);

        request = TEST_INVOKER.getRequests().get(1);
        sa.assertThat(request.getBaseDirectory().getAbsolutePath()).isEqualTo(APP_PATH.toAbsolutePath().toString());
        sa.assertThat(request.getGoals()).containsOnly("clean", "package");
        sa.assertThat(request.getProperties()).isEqualTo(Map.of(
            "skipTests", "true",
            "quarkus.native.container-build", "true"
        ));
        sa.assertThat(request.getOutputHandler(null)).isInstanceOf(MavenFileOutputHandler.class);

        sa.assertAll();
    }

    @Test
    public void shouldAddNativeProfileTest() {
        System.setProperty(QuarkusConfiguration.QUARKUS_NATIVE_BUILD, "true");

        new LocalQuarkusApp(dummyIb());
        Assertions.assertThat(TEST_INVOKER.getRequests()).hasSize(2);

        InvocationRequest request = TEST_INVOKER.getRequests().get(1);
        assertThat(request.getProfiles()).contains("native");
    }

    @Test
    public void shouldAddDependenciesTest() {
        final String groupId = "com.test";
        final String artifactId = "example";
        final String version = "1.0";
        new LocalQuarkusApp(dummyIb().dependencies(groupId + ":" + artifactId + ":" + version));
        verifyDependencies(groupId, artifactId, version);
    }

    @Test
    public void shouldAppendCamelQuarkusBomTest() {
        final String productizedVersion = "1.0.redhat-1";
        System.setProperty(QuarkusConfiguration.CAMEL_QUARKUS_VERSION, productizedVersion);

        TEST_INVOKER.mockExecution(() -> {
            Model model = Maven.loadPom(POM_PATH.toFile());
            DependencyManagement dm = new DependencyManagement();
            Dependency d = new Dependency();
            d.setGroupId("com.example");
            d.setArtifactId("test");
            d.setVersion("1.0");
            List<Dependency> dependencies = new ArrayList<>();
            dependencies.add(d);
            dm.setDependencies(dependencies);
            model.setDependencyManagement(dm);
            Maven.writePom(POM_PATH.toFile(), model);
        });

        new LocalQuarkusApp(dummyIb());

        final Model pom = Maven.loadPom(TestConfiguration.appLocation().resolve(name()).resolve("pom.xml").toFile());
        assertThat(pom.getDependencyManagement().getDependencies()).hasSize(2);

        Dependency d = pom.getDependencyManagement().getDependencies().get(1);
        assertThat(d.getGroupId()).isEqualTo(QuarkusConfiguration.camelQuarkusPlatformGroupId());
        assertThat(d.getArtifactId()).isEqualTo(QuarkusConfiguration.camelQuarkusPlatformArtifactId());
        assertThat(d.getVersion()).isEqualTo(QuarkusConfiguration.camelQuarkusPlatformVersion());
        assertThat(d.getType()).isEqualTo("pom");
        assertThat(d.getScope()).isEqualTo("import");
    }

    @Test
    public void shouldRemoveResteasyDependencyTest() {
        TEST_INVOKER.mockExecution(() -> {
            Model model = Maven.loadPom(POM_PATH.toFile());
            Dependency d = new Dependency();
            d.setGroupId("io.quarkus");
            d.setArtifactId("quarkus-resteasy");
            model.addDependency(d);
            d.setGroupId("com.test");
            d.setArtifactId("example");
            model.setDependencyManagement(new DependencyManagement());
            Maven.writePom(POM_PATH.toFile(), model);
        });

        new LocalQuarkusApp(dummyIb());

        Model pom = Maven.loadPom(POM_PATH.toFile());
        assertThat(pom.getDependencies()).hasSize(1);
        assertThat(pom.getDependencies().get(0).getArtifactId()).isEqualTo("example");
    }

    @Test
    public void shouldAddOpenshiftExtensionTest() {
        System.setProperty(OpenshiftConfiguration.USE_OPENSHIFT, "true");

        new LocalQuarkusApp(dummyIb());

        Assertions.assertThat(TEST_INVOKER.getRequests()).hasSize(2);

        InvocationRequest request = TEST_INVOKER.getRequests().get(0);
        assertThat(request.getProperties()).containsEntry("extensions", "openshift");
    }
}
