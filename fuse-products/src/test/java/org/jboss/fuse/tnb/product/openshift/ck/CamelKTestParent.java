package org.jboss.fuse.tnb.product.openshift.ck;

import org.jboss.fuse.tnb.common.openshift.OpenshiftClient;
import org.jboss.fuse.tnb.common.product.ProductType;
import org.jboss.fuse.tnb.product.ck.utils.CamelKSettings;
import org.jboss.fuse.tnb.product.ck.utils.CamelKSupport;
import org.jboss.fuse.tnb.product.openshift.OpenshiftTestParent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.fabric8.camelk.v1alpha1.KameletBinding;
import io.fabric8.camelk.v1alpha1.KameletBindingList;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodListBuilder;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
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
    private static final CustomResourceDefinitionContext kameletBindingCtx =
        CamelKSupport.kameletBindingCRDContext(CamelKSettings.KAMELET_API_VERSION_DEFAULT);
    protected NonNamespaceOperation<KameletBinding, KameletBindingList, Resource<KameletBinding>> kameletBindingClient;
    protected ExecutorService executor;

    @BeforeAll
    public static void setProduct() {
        setProduct(ProductType.CAMEL_K);
    }

    @BeforeEach
    public void prepare() {
        super.clear();
        executor = Executors.newSingleThreadExecutor();
        kameletBindingClient = OpenshiftClient.get().customResources(kameletBindingCtx, KameletBinding.class, KameletBindingList.class)
            .inNamespace(OpenshiftClient.get().getNamespace());
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
        expectServer.expect().get().withPath("/api/v1/namespaces/test/pods?labelSelector=" + Utils.toUrlEncoded("name=camel-k-operator"))
            .andReturn(200,
                new PodListBuilder()
                    .addNewItemLike(operatorPod(true))
                    .endItem().build()
            ).always();
    }
}
