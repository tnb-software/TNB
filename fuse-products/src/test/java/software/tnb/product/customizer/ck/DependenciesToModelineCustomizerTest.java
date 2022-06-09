package software.tnb.product.customizer.ck;

import static org.assertj.core.api.Assertions.assertThat;

import software.tnb.common.product.ProductType;
import software.tnb.product.ck.customizer.DependenciesToModelineCustomizer;
import software.tnb.product.customizer.Customizer;
import software.tnb.product.customizer.CustomizerTestParent;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
public class DependenciesToModelineCustomizerTest extends CustomizerTestParent {
    @Test
    public void shouldDoNothingForEmptyDependenciesTest() {
        customizer.doCustomize();

        assertThat(ib.getRouteBuilder().get().getComment()).isNotPresent();
    }

    @Test
    public void shouldAddMavenDependencyToModelineTest() {
        ib.dependencies("com.test:example");
        customizer.doCustomize();

        assertThat(ib.getRouteBuilder().get().getComment()).isPresent();
        assertThat(ib.getRouteBuilder().get().getComment().get().toString().trim()).isEqualTo("// camel-k: dependency=mvn:com.test:example");
    }

    @Test
    public void shouldAddDependencyWithVersionToModelineTest() {
        ib.dependencies("com.test:example:1.0");
        customizer.doCustomize();

        assertThat(ib.getRouteBuilder().get().getComment()).isPresent();
        assertThat(ib.getRouteBuilder().get().getComment().get().toString().trim()).isEqualTo("// camel-k: dependency=mvn:com.test:example:1.0");
    }

    @Test
    public void shouldAddGithubDependencyToModelineTest() {
        ib.dependencies("github:openshift-integration:camel-k-example-event-streaming:1.6.x-SNAPSHOT");
        customizer.doCustomize();

        assertThat(ib.getRouteBuilder().get().getComment()).isPresent();
        assertThat(ib.getRouteBuilder().get().getComment().get().toString().trim())
            .isEqualTo("// camel-k: dependency=github:openshift-integration:camel-k-example-event-streaming:1.6.x-SNAPSHOT");
    }

    @Test
    public void shouldAddMultipleDependenciesToModelineTest() {
        ib.dependencies("com.test:example", "com.test:model");
        customizer.doCustomize();

        assertThat(ib.getRouteBuilder().get().getComment()).isPresent();
        assertThat(ib.getRouteBuilder().get().getComment().get().toString().trim())
            .isEqualTo("// camel-k: dependency=mvn:com.test:example dependency=mvn:com.test:model");
    }

    @Override
    public Customizer newCustomizer() {
        return new DependenciesToModelineCustomizer();
    }

    @Override
    public ProductType product() {
        return ProductType.CAMEL_K;
    }
}
