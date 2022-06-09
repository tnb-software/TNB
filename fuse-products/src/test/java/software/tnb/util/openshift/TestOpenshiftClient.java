package software.tnb.util.openshift;

import software.tnb.common.openshift.OpenshiftClient;

import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.OpenShiftConfigBuilder;
import io.fabric8.openshift.client.server.mock.OpenShiftMockServer;

public final class TestOpenshiftClient extends OpenshiftClient {
    private TestOpenshiftClient(OpenShiftConfig config) {
        super(config);
    }

    public static void setServer(OpenShiftMockServer server) {
        client = new TestOpenshiftClient(new OpenShiftConfigBuilder()
            .withMasterUrl(server.url("/"))
            .withNamespace("test")
            .withTrustCerts(true)
            .build()
        );
    }
}
