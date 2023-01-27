package software.tnb.common.openshift;

import software.tnb.common.config.TestConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * This class returns either the OpenshiftClient directly, or ThreadLocal variant if running in parallel.
 */
public class OpenshiftClientWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftClientWrapper.class);
    private final ThreadLocal<OpenshiftClient> threadLocalClient = new ThreadLocal<>();
    private OpenshiftClient openshiftClient;
    private final Supplier<OpenshiftClient> supplier;

    public OpenshiftClientWrapper(Supplier<OpenshiftClient> clientInit) {
        this.supplier = clientInit;
        init();
    }

    public void init() {
        if (TestConfiguration.parallel()) {
            threadLocalClient.set(supplier.get());
            Thread.currentThread().setName(Thread.currentThread().getName() + "|" + threadLocalClient.get().getNamespace());
        } else {
            openshiftClient = supplier.get();
        }
    }

    public OpenshiftClient getClient() {
        return TestConfiguration.parallel() ? threadLocalClient.get() : openshiftClient;
    }

    public void closeClient() {
        if (TestConfiguration.parallel()) {
            LOG.trace("Closing OpenShift client in thread " + Thread.currentThread().getName());
            Thread.currentThread().setName(Thread.currentThread().getName().replace("|" + getClient().getNamespace(), ""));
        }
        getClient().close();
        threadLocalClient.set(null);
        openshiftClient = null;
    }
}
