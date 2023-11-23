package software.tnb.product.customizer.ck;

import static org.junit.jupiter.api.Assertions.fail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import software.tnb.product.ck.customizer.LocalDependencyCustomizer;
import software.tnb.product.cq.configuration.QuarkusConfiguration;
import software.tnb.product.integration.builder.IntegrationBuilder;
import software.tnb.product.openshift.ck.CamelKTestParent;
import software.tnb.util.maven.TestMaven;
import software.tnb.util.maven.TestMavenInvoker;
import software.tnb.util.openshift.TestOpenshiftClient;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.apache.maven.model.Dependency;
import org.apache.maven.shared.invoker.InvocationRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.client.utils.Utils;

@Tag("unit")
public class LocalDependencyCustomizerTest extends CamelKTestParent {
    private static final TestMavenInvoker TEST_INVOKER = new TestMavenInvoker();

    @BeforeAll
    public static void setupMaven() {
        TestMaven.setupTestMaven(TEST_INVOKER);
    }

    @AfterEach
    public void cleanup() {
        TEST_INVOKER.clearRequests();
    }

    @Test
    public void shouldFailForDependencyWithNoVersionTest() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            new LocalDependencyCustomizer("com.test:example", Path.of(""), null).doCustomize());
    }

    @Test
    public void shouldFailForNonExistentFolderTest() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            new LocalDependencyCustomizer("com.test:example:1.0", Path.of("nonexistent"), null).doCustomize());
    }

    @Test
    public void shouldBuildProjectWhenJarFileIsMissingTest() {
        Path dir = createDirectory("shouldBuildProjectWhenJarFileIsMissingTest");
        try {
            new LocalDependencyCustomizer("com.test:example:1.0", dir, "test.jar").doCustomize();
        } catch (Exception ignored) {
        }

        assertThat(TEST_INVOKER.getRequests()).hasSize(1);
        final InvocationRequest req = TEST_INVOKER.getRequests().get(0);
        assertThat(req.getBaseDirectory()).isEqualTo(dir.toFile());
        assertThat(req.getGoals()).containsOnly("clean", "package");
        assertThat(req.getProperties()).isEqualTo(Map.of(
            "skipTests", "true",
            "quarkus.version", QuarkusConfiguration.DEFAULT_QUARKUS_VERSION,
            "camel-quarkus.version", QuarkusConfiguration.DEFAULT_CAMEL_QUARKUS_VERSION
        ));
    }

    @Test
    public void shouldFailWhenTheJarFileIsMissingAfterBuildingTest() {
        Path dir = createDirectory("shouldFailWhenTheJarFileIsMissingAfterBuildingTest");

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
            new LocalDependencyCustomizer("com.test:example:1.0", dir, "test.jar").doCustomize());
    }

    @Test
    @Disabled("After upgrading to 6.x client, the upload command changed, need to fix the expectation in expectServer")
    public void shouldCopyFilesToOperatorTest() {
        final String groupId = "com.test";
        final String artifactId = "example";
        final String version = "1.0";
        final String fileName = "test.jar";
        Path dir = createDirectory("shouldCopyFilesToOperatorTest");

        mockMavenBuild(dir, fileName);

        TestOpenshiftClient.setServer(expectServer);

        expectOperatorGet();

        List.of("pom", "jar").forEach(type -> expectFileUpload(groupId, artifactId, version, type));

        final LocalDependencyCustomizer c = new LocalDependencyCustomizer(groupId + ":" + artifactId + ":" + version, dir, fileName);
        IntegrationBuilder ib = dummyIb();
        c.setIntegrationBuilder(ib);
        c.doCustomize();

        assertThat(expectServer.getRequestCount() - requestCount).isEqualTo(3);
    }

    @Test
    @Disabled("After upgrading to 6.x client, the upload command changed, need to fix the expectation in expectServer")
    public void shouldAddToDependenciesTest() {
        final String groupId = "com.test";
        final String artifactId = "example";
        final String version = "1.0";
        final String fileName = "test.jar";
        Path dir = createDirectory("shouldAddToDependenciesTest");

        mockMavenBuild(dir, fileName);

        TestOpenshiftClient.setServer(expectServer);

        expectOperatorGet();

        List.of("pom", "jar").forEach(type -> expectFileUpload(groupId, artifactId, version, type));

        final LocalDependencyCustomizer c = new LocalDependencyCustomizer(groupId + ":" + artifactId + ":" + version, dir, fileName);
        IntegrationBuilder ib = dummyIb();
        c.setIntegrationBuilder(ib);
        c.doCustomize();

        assertThat(ib.getDependencies()).hasSize(1);
        final Dependency d = ib.getDependencies().get(0);
        assertThat(d.getGroupId()).isEqualTo(groupId);
        assertThat(d.getArtifactId()).isEqualTo(artifactId);
        assertThat(d.getVersion()).isEqualTo(version);
    }

    private void mockMavenBuild(Path dir, String jar) {
        TEST_INVOKER.mockExecution(() -> {
            try {
                Files.createDirectory(dir.resolve("target"));
                Files.createFile(dir.resolve("pom.xml"));
                Files.createFile(dir.resolve("target").resolve(jar));
            } catch (Exception e) {
                fail("Unable to create files", e);
            }
        });
    }

    private Path createDirectory(String dir) {
        Path directory = Paths.get("target", dir);
        try {
            Files.createDirectory(directory);
        } catch (IOException e) {
            fail("Unable to create directory", e);
        }
        return directory;
    }

    private void expectFileUpload(String groupId, String artifactId, String version, String type) {
        final String dir = String.format("/tmp/artifacts/m2/%s/%s/%s", groupId.replaceAll("\\.", "/"), artifactId, version);
        String command = String.format("mkdir -p '%s' && base64 -d - > '%s/%s-%s.%s'", dir, dir, artifactId, version, type);
        expectServer.expect()
            .get().withPath("/api/v1/namespaces/test/pods/camel-k/exec?command=sh&command=-c&command=" + Utils.toUrlEncoded(command)
                + "&stdin=true&stderr=true")
            .andUpgradeToWebSocket().open("").done().always();
    }
}
