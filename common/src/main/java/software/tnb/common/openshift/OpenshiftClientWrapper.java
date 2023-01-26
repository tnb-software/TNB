package software.tnb.common.openshift;

import software.tnb.common.config.TestConfiguration;

import java.util.function.Supplier;

/**
 * This class returns either the OpenshiftClient directly, or ThreadLocal variant if running in parallel.
 */
public class OpenshiftClientWrapper {
    private ThreadLocal<OpenshiftClient> threadLocalClient;
    private OpenshiftClient openshiftClient;

    public OpenshiftClientWrapper(Supplier<OpenshiftClient> clientInit) {
        if (TestConfiguration.parallel()) {
            threadLocalClient = ThreadLocal.withInitial(clientInit);
        } else {
            openshiftClient = clientInit.get();
        }
    }

    public OpenshiftClient getClient() {
        return TestConfiguration.parallel() ? threadLocalClient.get() : openshiftClient;
    }

    public void closeClient() {
        getClient().close();
        if (threadLocalClient != null) {
            threadLocalClient.set(null);
        }
        openshiftClient = null;
    }
}
