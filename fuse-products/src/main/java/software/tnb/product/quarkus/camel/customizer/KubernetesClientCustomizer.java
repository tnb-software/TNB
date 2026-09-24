package software.tnb.product.quarkus.camel.customizer;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.openshift.OpenshiftClient;

import java.util.Map;

/**
 * Kubernetes client properties moved to build time properties for quarkus 3.35+, so they need to be in application.properties when the app is built.
 */
public class KubernetesClientCustomizer extends CamelQuarkusCustomizer {
    @Override
    public void customize() {
        if (OpenshiftConfiguration.isOpenshift()) {
            getIntegrationBuilder().addToApplicationProperties(Map.of(
                "quarkus.kubernetes-client.api-server-url", OpenshiftClient.get().getConfiguration().getMasterUrl(),
                "quarkus.kubernetes-client.token", OpenshiftClient.get().getConfiguration().getAutoOAuthToken(),
                "quarkus.kubernetes-client.namespace", OpenshiftClient.get().getNamespace(),
                "quarkus.kubernetes-client.trust-certs", "true"
            ));
        }
    }
}
