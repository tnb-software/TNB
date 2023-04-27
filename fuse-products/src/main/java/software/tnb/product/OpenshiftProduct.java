package software.tnb.product;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.JUnitUtils;
import software.tnb.common.utils.WaitUtils;

import org.junit.jupiter.api.extension.ExtensionContext;

public abstract class OpenshiftProduct extends Product {

    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        setupProduct();
        WaitUtils.waitFor(this::isReady, 60, 5000L, "Waiting until the " + this.getClass().getSimpleName() + " is ready");
    }

    public void afterAll(ExtensionContext extensionContext) throws Exception {
        try {
            if (!JUnitUtils.isExtensionStillNeeded(extensionContext, this.getClass())) {
                teardownProduct();
                deleteNamespace();
            }
        } catch (Exception e) {
            // Print the stackstace as it is swallowed by junit somehow
            e.printStackTrace();
            throw e;
        }
    }

    private void deleteNamespace() {
        if (OpenshiftConfiguration.openshiftNamespaceDelete()) {
            OpenshiftClient.get().deleteNamespace();
        }
    }

    public abstract boolean isReady();
}
