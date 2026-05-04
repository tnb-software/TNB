package software.tnb.service;

import static org.assertj.core.api.Assertions.assertThat;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.deployment.WithOperatorHub;
import software.tnb.common.service.WithServiceDefinition;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
public class ServiceDefinitionTest {

    private final String nameProperty = String.format(WithServiceDefinition.NAME_PROPERTY_FORMAT, "plainparent");
    private final String versionProperty = String.format(WithServiceDefinition.VERSION_PROPERTY_FORMAT, "plainparent");

    @AfterEach
    void clearProperties() {
        System.clearProperty(nameProperty);
        System.clearProperty(versionProperty);
    }

    // --- serviceName tests ---

    @Test
    void plainServiceNameShouldBeClassDerived() {
        assertThat(new PlainChild().serviceName()).isEqualTo("PlainParent");
    }

    @Test
    void dockerImageWithVersionTagShouldUseClassDerivedName() {
        assertThat(new DockerChildVersioned().serviceName()).isEqualTo("DockerParent");
    }

    @Test
    void dockerImageWithLatestTagShouldUseImageName() {
        assertThat(new DockerChildLatest().serviceName()).isEqualTo("mysql-80");
    }

    @Test
    void dockerImageWithNoTagShouldUseImageName() {
        assertThat(new DockerChildNoTag().serviceName()).isEqualTo("mysql-80");
    }

    @Test
    void operatorWithCsvShouldUseCsvName() {
        assertThat(new OperatorChild("amq-streams.v2.10.0").serviceName()).isEqualTo("amq-streams");
    }

    @Test
    void operatorWithNullCsvShouldFallBackToClassDerived() {
        assertThat(new OperatorChild(null).serviceName()).isEqualTo("OperatorParent");
    }

    @Test
    void systemPropertyShouldOverrideServiceName() {
        System.setProperty(nameProperty, "custom-name");
        assertThat(new PlainChild().serviceName()).isEqualTo("custom-name");
    }

    // --- serviceVersion tests ---

    @Test
    void plainServiceVersionShouldBeNull() {
        assertThat(new PlainChild().serviceVersion()).isNull();
    }

    @Test
    void dockerImageShouldUseTagAsVersion() {
        assertThat(new DockerChildVersioned().serviceVersion()).isEqualTo("26.0-6");
    }

    @Test
    void dockerImageWithLatestTagShouldReturnLatest() {
        assertThat(new DockerChildLatest().serviceVersion()).isEqualTo("latest");
    }

    @Test
    void dockerImageWithNoTagShouldReturnNull() {
        assertThat(new DockerChildNoTag().serviceVersion()).isNull();
    }

    @Test
    void operatorWithCsvShouldUseCsvVersion() {
        assertThat(new OperatorChild("amq-streams.v2.10.0").serviceVersion()).isEqualTo("v2.10.0");
    }

    @Test
    void operatorWithNullCsvShouldReturnNull() {
        assertThat(new OperatorChild(null).serviceVersion()).isNull();
    }

    @Test
    void operatorWithNullCsvAndDockerImageShouldFallBackToImageTag() {
        assertThat(new OperatorDockerChild(null, "quay.io/service:3.0").serviceVersion()).isEqualTo("3.0");
    }

    @Test
    void operatorWithCsvShouldTakePrecedenceOverDockerImage() {
        assertThat(new OperatorDockerChild("amq-streams.v2.10.0", "quay.io/service:3.0").serviceVersion()).isEqualTo("v2.10.0");
    }

    @Test
    void systemPropertyShouldOverrideServiceVersion() {
        System.setProperty(versionProperty, "99.0");
        assertThat(new PlainChild().serviceVersion()).isEqualTo("99.0");
    }

    // --- test stubs ---

    abstract static class PlainParent implements WithServiceDefinition {
    }

    static class PlainChild extends PlainParent {
    }

    abstract static class DockerParent implements WithServiceDefinition, WithDockerImage {
    }

    static class DockerChildVersioned extends DockerParent {
        @Override
        public String defaultImage() {
            return "quay.io/fuse_qe/keycloak:26.0-6";
        }
    }

    static class DockerChildLatest extends DockerParent {
        @Override
        public String defaultImage() {
            return "registry.redhat.io/rhel8/mysql-80:latest";
        }
    }

    static class DockerChildNoTag extends DockerParent {
        @Override
        public String defaultImage() {
            return "registry.redhat.io/rhel8/mysql-80";
        }
    }

    abstract static class OperatorParent implements WithServiceDefinition, WithOperatorHub {
        private final String csv;

        OperatorParent(String csv) {
            this.csv = csv;
        }

        @Override
        public String operatorName() {
            return "test-operator";
        }

        @Override
        public String currentCSV() {
            return csv;
        }
    }

    static class OperatorChild extends OperatorParent {
        OperatorChild(String csv) {
            super(csv);
        }
    }

    abstract static class OperatorDockerParent implements WithServiceDefinition, WithOperatorHub, WithDockerImage {
        private final String csv;
        private final String image;

        OperatorDockerParent(String csv, String image) {
            this.csv = csv;
            this.image = image;
        }

        @Override
        public String operatorName() {
            return "test-operator";
        }

        @Override
        public String currentCSV() {
            return csv;
        }

        @Override
        public String defaultImage() {
            return image;
        }
    }

    static class OperatorDockerChild extends OperatorDockerParent {
        OperatorDockerChild(String csv, String image) {
            super(csv, image);
        }
    }
}
