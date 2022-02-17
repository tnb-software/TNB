package org.jboss.fuse.tnb.product.integration;

import org.jboss.fuse.tnb.product.git.GitRepository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class GitIntegrationBuilder extends IntegrationBuilder {
    private Optional<String> projectPath = Optional.empty();
    private String repositoryUrl;
    private String branch = "main";
    private Map<String, String> mavenProperties = new HashMap<>();
    private Optional<String> finalName = Optional.empty();
    private Map<String, String> javaProperties = new HashMap<>();
    private String projectVersion;
    private String parentVersion;
    private List<Consumer<Path>> preMavenBuildCustomizers = new ArrayList<>();

    public GitIntegrationBuilder(String repositoryUrl) {
        super(repositoryUrl
            .substring(
                repositoryUrl.lastIndexOf("/") + 1,
                repositoryUrl.lastIndexOf(".git")));
        this.repositoryUrl = repositoryUrl;
    }

    public GitIntegrationBuilder withProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
        return this;
    }

    public GitIntegrationBuilder withParentVersion(String projectVersion) {
        this.parentVersion = projectVersion;
        return this;
    }

    public GitIntegrationBuilder withProjectPath(String path) {
        projectPath = Optional.of(path);
        return this;
    }

    public GitIntegrationBuilder withBranch(String branch) {
        this.branch = branch;
        return this;
    }

    public GitIntegrationBuilder withFinalName(String finalName) {
        this.finalName = Optional.of(finalName);
        return this;
    }

    public GitIntegrationBuilder withMavenProperty(String key, String value) {
        mavenProperties.put(key, value);
        return this;
    }

    public GitIntegrationBuilder withMavenProperties(Map<String, String> props) {
        mavenProperties.putAll(props);
        return this;
    }

    public GitIntegrationBuilder withJavaProperty(String key, String value) {
        javaProperties.put(key, value);
        return this;
    }

    public GitIntegrationBuilder withJavaProperties(Map<String, String> props) {
        javaProperties.putAll(props);
        return this;
    }

    public GitIntegrationBuilder withPreMavenBuildCustomizer(Consumer<Path> customizer) {
        preMavenBuildCustomizers.add(customizer);
        return this;
    }

    public Optional<String> getProjectPath() {
        return projectPath;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public String getBranch() {
        return branch;
    }

    public Map<String, String> getMavenProperties() {
        return mavenProperties;
    }

    public Map<String, String> getJavaProperties() {
        return javaProperties;
    }

    public Optional<String> getFinalName() {
        return finalName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public String getParentVersion() {
        return parentVersion;
    }

    public String getIntegrationName() {
        return GitRepository.getName(this);
    }

    public List<Consumer<Path>> getPreMavenBuildCustomizers() {
        return preMavenBuildCustomizers;
    }
}
