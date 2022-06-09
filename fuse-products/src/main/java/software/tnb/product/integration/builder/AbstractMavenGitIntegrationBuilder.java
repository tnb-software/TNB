package software.tnb.product.integration.builder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractMavenGitIntegrationBuilder<SELF extends AbstractMavenGitIntegrationBuilder<SELF>>
    extends AbstractGitIntegrationBuilder<SELF> {

    private final Map<String, String> mavenProperties = new HashMap<>();
    private final Map<String, String> javaProperties = new HashMap<>();
    private final List<Consumer<Path>> preMavenBuildCustomizers = new ArrayList<>();
    private String finalName;
    private String projectVersion;
    private String parentVersion;

    public AbstractMavenGitIntegrationBuilder(String integrationName) {
        super(integrationName);
    }

    public SELF withProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
        return self();
    }

    public SELF withParentVersion(String projectVersion) {
        this.parentVersion = projectVersion;
        return self();
    }

    public SELF withFinalName(String finalName) {
        this.finalName = finalName;
        return self();
    }

    public SELF withMavenProperty(String key, String value) {
        mavenProperties.put(key, value);
        return self();
    }

    public SELF withMavenProperties(Map<String, String> props) {
        mavenProperties.putAll(props);
        return self();
    }

    public SELF withJavaProperty(String key, String value) {
        javaProperties.put(key, value);
        return self();
    }

    public SELF withJavaProperties(Map<String, String> props) {
        javaProperties.putAll(props);
        return self();
    }

    public SELF withPreMavenBuildCustomizer(Consumer<Path> customizer) {
        preMavenBuildCustomizers.add(customizer);
        return self();
    }

    public Map<String, String> getMavenProperties() {
        return mavenProperties;
    }

    public Map<String, String> getJavaProperties() {
        return javaProperties;
    }

    public Optional<String> getFinalName() {
        return Optional.ofNullable(finalName);
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public String getParentVersion() {
        return parentVersion;
    }

    public List<Consumer<Path>> getPreMavenBuildCustomizers() {
        return preMavenBuildCustomizers;
    }

}
