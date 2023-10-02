package software.tnb.product.openshift;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.product.parent.TestParent;
import software.tnb.util.openshift.TestOpenshiftClient;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collections;
import java.util.HashMap;

import io.fabric8.kubernetes.client.server.mock.KubernetesCrudDispatcher;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import io.fabric8.mockwebserver.Context;
import okhttp3.mockwebserver.MockWebServer;

/**
 * Test parent for all openshift related tests.
 *
 * Starts the openshift mock servers (crud server and expect server)
 */
public class OpenshiftTestParent extends TestParent {
    private static final KubernetesCrudDispatcher DISPATCHER = new KubernetesCrudDispatcher(Collections.emptyList());
    protected static KubernetesMockServer crudServer;
    protected static KubernetesMockServer expectServer;
    protected int requestCount = 0;

    @BeforeAll
    public static void init() {
        crudServer = new KubernetesMockServer(new Context(), new MockWebServer(), new HashMap<>(), DISPATCHER, false);
        expectServer = new KubernetesMockServer(false);
        crudServer.init();
        expectServer.init();

        System.setProperty(OpenshiftConfiguration.OPENSHIFT_NAMESPACE, "test");
        System.setProperty(OpenshiftConfiguration.USE_OPENSHIFT, "true");
    }

    @BeforeEach
    public void clear() {
        DISPATCHER.getMap().clear();
        expectServer.clearExpectations();
        // There is no method for clearing the requests
        requestCount = expectServer.getRequestCount();
        TestOpenshiftClient.setServer(crudServer);
    }

    @AfterAll
    public static void destroy() {
        crudServer.destroy();
        expectServer.destroy();

        System.clearProperty(OpenshiftConfiguration.OPENSHIFT_NAMESPACE);
        System.clearProperty(OpenshiftConfiguration.USE_OPENSHIFT);
    }
}
