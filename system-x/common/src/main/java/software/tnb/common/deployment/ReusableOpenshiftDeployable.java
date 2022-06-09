package software.tnb.common.deployment;

import software.tnb.common.utils.JUnitUtils;

import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Resource that is able to be reused between multiple tests to avoid multiple deploys/undeploys.
 */
public interface ReusableOpenshiftDeployable extends OpenshiftDeployable {
    /**
     * Cleanup the resource itself after running the tests - delete database tables, remove files, etc.
     */
    void cleanup();

    default void beforeAll(ExtensionContext extensionContext) throws Exception {
        // Deploy does "deploy" (if it is not already deployed) + wait until it's ready
        deploy();
        openResources();
    }

    default void afterAll(ExtensionContext extensionContext) throws Exception {
        if (JUnitUtils.isExtensionStillNeeded(extensionContext, this.getClass())) {
            cleanup();
        } else {
            undeploy();
        }
        closeResources();
    }
}
