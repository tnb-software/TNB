package software.tnb.cryostat.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;
import software.tnb.cryostat.client.CryostatClient;
import software.tnb.cryostat.client.local.LocalCryostatClient;
import software.tnb.cryostat.service.Cryostat;

import com.google.auto.service.AutoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AutoService(Cryostat.class)
public class LocalCryostat extends Cryostat implements ContainerDeployable<CryostatContainer>, WithDockerImage {
    private static final String JMX_DB_PASSWORD = UUID.randomUUID().toString();
    private final CryostatContainer container = new CryostatContainer(image(), containerEnvironment());

    /**
     * Open all resources needed after the service is deployed - initialize clients and stuff.
     */
    @Override
    public void openResources() {
        final HTTPUtils client = HTTPUtils.getInstance(HTTPUtils.trustAllSslClient());
        WaitUtils.waitFor(new Waiter(() -> client.get(String.format("%s/health", connectionUrl()), false).isSuccessful()
            , "wait for container ready"));
        validation().init();
    }

    /**
     * Close all resources used after before the service is undeployed.
     */
    @Override
    public void closeResources() {

    }

    @Override
    public String connectionUrl() {
        return String.format("http://%s:%d", container.getHost(), getPortMapping(8181));
    }

    @Override
    public CryostatClient client() {
        return new LocalCryostatClient(connectionUrl());
    }

    @Override
    public int getPortMapping(int port) {
        return port; //use fixed port because of the cryostat container using host net
    }

    protected Map<String, String> containerEnvironment() {
        final Map<String, String> env = new HashMap<>();
        env.put("CRYOSTAT_DISABLE_JMX_AUTH", "true");
        env.put("CRYOSTAT_ALLOW_UNTRUSTED_SSL", "true");
        env.put("CRYOSTAT_DISABLE_SSL", "true");
        env.put("CRYOSTAT_JMX_CREDENTIALS_DB_PASSWORD", JMX_DB_PASSWORD);
        return env;
    }

    public String defaultImage() {
        return "registry.redhat.io/cryostat-tech-preview/cryostat-rhel8:latest";
    }

    @Override
    public CryostatContainer container() {
        return container;
    }
}
