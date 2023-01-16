package software.tnb.util.openshift;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.openshift.OpenshiftClientWrapper;

import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.OpenShiftConfigBuilder;
import io.fabric8.openshift.client.server.mock.OpenShiftMockServer;

public final class TestOpenshiftClient extends OpenshiftClient {
    private TestOpenshiftClient(OpenShiftConfig config) {
        super(config);
    }

    public static void setServer(OpenShiftMockServer server) {
        clientWrapper = new OpenshiftClientWrapper(() -> new TestOpenshiftClient(new OpenShiftConfigBuilder()
            .withMasterUrl(server.url("/"))
            .withNamespace("test")
            .withTrustCerts(true)
            .build()
        ));
    }
}
