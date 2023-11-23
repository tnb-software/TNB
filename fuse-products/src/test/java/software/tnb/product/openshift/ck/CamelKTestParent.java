package software.tnb.product.openshift.ck;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.product.ProductType;
import software.tnb.product.openshift.OpenshiftTestParent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import org.apache.camel.v1.IntegrationPlatform;
import org.apache.camel.v1.IntegrationPlatformStatus;
import org.apache.camel.v1alpha1.KameletBinding;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.DefaultKubernetesResourceList;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodListBuilder;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.utils.Utils;

/**
 * Test parent for Camel-K related tests.
 *
 * Contains:
 *   * KameletBinding client for manipulating KameletBindings (not possible via fabric8 camel-k-client)
 *   * executor in case you want to run something in a different thread (usually to test methods that are waiting for something)
 *   * useful shared methods
 */
public class CamelKTestParent extends OpenshiftTestParent {
    protected NonNamespaceOperation<KameletBinding, KubernetesResourceList<KameletBinding>, Resource<KameletBinding>> kameletBindingClient;
    protected ExecutorService executor;

    @BeforeAll
    public static void setProduct() {
        setProduct(ProductType.CAMEL_K);
    }

    @BeforeEach
    public void prepare() {
        super.clear();
        executor = Executors.newSingleThreadExecutor();
        kameletBindingClient = OpenshiftClient.get().resources(KameletBinding.class).inNamespace(OpenshiftClient.get().getNamespace());
    }

    @AfterEach
    public void shutdown() {
        executor.shutdownNow();
    }

    protected Pod operatorPod(boolean ready) {
        return new PodBuilder()
            .withNewMetadata()
            .withLabels(Map.of("name", "camel-k-operator"))
            .withName("camel-k")
            .endMetadata()
            .withNewSpec()
            .addToContainers(new ContainerBuilder().build())
            .endSpec()
            .withNewStatus()
            .addNewContainerStatus()
            .withReady(ready)
            .endContainerStatus()
            .endStatus()
            .build();
    }

    protected void expectOperatorGet() {
        expectServer.expect().get().withPath("/api/v1/namespaces/test/pods/camel-k")
            .andReturn(200, operatorPod(true)).always();
        expectServer.expect().get().withPath("/api/v1/namespaces/test/pods?labelSelector=" + Utils.toUrlEncoded("name=camel-k-operator"))
            .andReturn(200,
                new PodListBuilder()
                    .addNewItemLike(operatorPod(true))
                    .endItem().build()
            ).always();
    }

    protected void expectIntegrationPlatform() {
        final DefaultKubernetesResourceList<IntegrationPlatform> list = new DefaultKubernetesResourceList<>();
        IntegrationPlatform ip = new IntegrationPlatform();
        IntegrationPlatformStatus status = new IntegrationPlatformStatus();
        status.setPhase("Ready");
        ip.setStatus(status);
        list.setItems(List.of(ip));
        expectServer.expect().get().withPath("/apis/camel.apache.org/v1/namespaces/test/integrationplatforms").andReturn(200, list).always();
    }
}
