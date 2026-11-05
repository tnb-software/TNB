package software.tnb.product.quarkus.camel.customizer;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.product.customizer.ProductsCustomizer;

import java.util.Map;

/**
 * Kubernetes client properties moved to build time properties for quarkus 3.35+, so they need to be in application.properties when the app is built.
 * Note: It is ProductsCustomizer so it is applied for both Camel Quarkus and Quarkus CXF products.
 */
public class KubernetesClientCustomizer extends ProductsCustomizer {
    @Override
    public void customizeQuarkus() {
        if (OpenshiftConfiguration.isOpenshift()) {
            getIntegrationBuilder().addToApplicationProperties(Map.of(
                "quarkus.kubernetes-client.api-server-url", OpenshiftClient.get().getConfiguration().getMasterUrl(),
                "quarkus.kubernetes-client.token", OpenshiftClient.get().getConfiguration().getAutoOAuthToken(),
                "quarkus.kubernetes-client.namespace", OpenshiftClient.get().getNamespace(),
                "quarkus.kubernetes-client.trust-certs", "true"
            ));
        }
    }

    @Override
    public void customizeSpringboot() {

    }
}
