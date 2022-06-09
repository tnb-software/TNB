package software.tnb.product.maven;

import static org.junit.jupiter.api.Assertions.fail;

import static org.assertj.core.api.Assertions.assertThat;

import software.tnb.common.config.TestConfiguration;
import software.tnb.common.product.ProductType;
import software.tnb.product.parent.TestParent;
import software.tnb.product.util.maven.Maven;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Reader;

import java.io.StringReader;
import java.util.List;

@Tag("unit")
public class MavenTest extends TestParent {
    @AfterEach
    public void clearProperties() {
        List.of(TestConfiguration.MAVEN_SETTINGS, TestConfiguration.MAVEN_REPOSITORY).forEach(System::clearProperty);
    }

    @Test
    public void shouldCreateDependencyFromStringWithoutVersionTest() {
        final String groupId = "com.test";
        final String artifactId = "example";

        verifyDependency(Maven.createDependency(groupId + ":" + artifactId), groupId, artifactId, null);
    }

    @Test
    public void shouldCreateDependencyFromStringWithVersionTest() {
        final String groupId = "com.test";
        final String artifactId = "example";
        final String version = "1.0";

        verifyDependency(Maven.createDependency(groupId + ":" + artifactId + ":" + version), groupId, artifactId, version);
    }

    @Test
    public void shouldCreateDependencyFromGithubStringWithoutVersionTest() {
        final String owner = "openshift-integration";
        final String repository = "camel-k-example-event-streaming";

        Dependency d = Maven.createDependency("github:" + owner + ":" + repository);
        verifyDependency(d, owner, repository, null);
        assertThat(d.getType()).isEqualTo("github");
    }

    @Test
    public void shouldCreateDependencyFromGithubStringWithVersionTest() {
        final String owner = "openshift-integration";
        final String repository = "camel-k-example-event-streaming";
        final String branch = "1.6.x-SNAPSHOT";

        Dependency d = Maven.createDependency("github:" + owner + ":" + repository + ":" + branch);
        verifyDependency(d, owner, repository, branch);
        assertThat(d.getType()).isEqualTo("github");
    }

    @ParameterizedTest
    @EnumSource(ProductType.class)
    public void shouldCreatePlatformSpecificCamelDependencyTest(ProductType type) {
        final String component = "rest";
        setProduct(type);
        Dependency d = Maven.createDependency(component);

        switch (type) {
            case CAMEL_SPRINGBOOT:
                verifyDependency(d, "org.apache.camel.springboot", "camel-" + component + "-starter", null);
                break;
            case CAMEL_QUARKUS:
            case CAMEL_K:
                verifyDependency(d, "org.apache.camel.quarkus", "camel-quarkus-" + component, null);
                break;
            default:
                throw new IllegalArgumentException("Implement new switch case for " + type.name());
        }
        assertThat(d.getVersion()).isNull();
    }

    @Test
    public void shouldCreateDependencyWithExclusionTest() {
        final String groupId = "com.test";
        final String artifactId = "example";
        final String exclusionGroupId = "com.github";
        final String exclusionArtifactId = "excluded";

        Dependency d = Maven.createDependency(groupId + ":" + artifactId, exclusionGroupId + ":" + exclusionArtifactId);
        verifyDependency(d, groupId, artifactId, null);
        assertThat(d.getExclusions()).hasSize(1);

        Exclusion e = d.getExclusions().get(0);
        assertThat(e.getGroupId()).isEqualTo(exclusionGroupId);
        assertThat(e.getArtifactId()).isEqualTo(exclusionArtifactId);
    }

    @Test
    public void shouldCreateSettingsFileWithMirrorTest() {
        final String repo = "http://example.com";
        final String mirrorOf = "*";
        System.setProperty(TestConfiguration.MAVEN_REPOSITORY, repo + "@mirrorOf=" + mirrorOf);

        Settings settings = createSettings();

        assertThat(settings.getMirrors()).hasSize(1);
        Mirror m = settings.getMirrors().get(0);
        assertThat(m.getUrl()).isEqualTo(repo);
        assertThat(m.getMirrorOf()).isEqualTo(mirrorOf);
    }

    @Test
    public void shouldCreateSettingsFileWithProfileTest() {
        final String repo = "http://example.com";
        System.setProperty(TestConfiguration.MAVEN_REPOSITORY, repo);

        Settings settings = createSettings();

        assertThat(settings.getMirrors()).hasSize(0);

        assertThat(settings.getProfiles()).hasSize(1);
        Profile p = settings.getProfiles().get(0);

        assertThat(p.getRepositories()).hasSize(1);
        Repository r = p.getRepositories().get(0);

        assertThat(r.getUrl()).isEqualTo(repo);
        assertThat(r.getReleases().isEnabled()).isTrue();
        assertThat(r.getSnapshots().isEnabled()).isTrue();

        assertThat(p.getPluginRepositories()).hasSize(1);
        r = p.getPluginRepositories().get(0);
        assertThat(r.getUrl()).isEqualTo(repo);
        assertThat(r.getReleases().isEnabled()).isTrue();
        assertThat(r.getSnapshots().isEnabled()).isTrue();
    }

    @Test
    public void shouldActivateProfileForCamelKTest() {
        setProduct(ProductType.CAMEL_K);
        System.setProperty(TestConfiguration.MAVEN_REPOSITORY, "http://example.com");

        Settings settings = createSettings();

        assertThat(settings.getActiveProfiles()).hasSize(1);
        assertThat(settings.getActiveProfiles()).contains(TestConfiguration.mavenRepositoryId());
    }

    private Settings createSettings() {
        try {
            return new SettingsXpp3Reader().read(new StringReader(Maven.createSettingsXmlFile()));
        } catch (Exception e) {
            fail("Unable to read settings", e);
        }
        // Unreachable
        return new Settings();
    }
}
