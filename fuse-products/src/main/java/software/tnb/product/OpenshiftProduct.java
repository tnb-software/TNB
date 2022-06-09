package software.tnb.product;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.JUnitUtils;
import software.tnb.common.utils.WaitUtils;

import org.junit.jupiter.api.extension.ExtensionContext;

public abstract class OpenshiftProduct extends Product {

    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        OpenshiftClient.get().createNamespace(); // ensure namespace exists
        setupProduct();
        WaitUtils.waitFor(this::isReady, 60, 5000L, "Waiting until the " + this.getClass().getSimpleName() + " is ready");
    }

    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (!JUnitUtils.isExtensionStillNeeded(extensionContext, this.getClass())) {
            teardownProduct();
            deleteNamespace();
        }
    }

    private void deleteNamespace() {
        if (OpenshiftConfiguration.isTemporaryNamespace() || OpenshiftConfiguration.openshiftNamespaceDelete()) {
            OpenshiftClient.get().deleteNamespace();
        }
    }

    public abstract boolean isReady();
}
