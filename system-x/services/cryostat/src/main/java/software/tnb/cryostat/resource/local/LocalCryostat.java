package software.tnb.cryostat.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;
import software.tnb.cryostat.client.CryostatClient;
import software.tnb.cryostat.client.local.LocalCryostatClient;
import software.tnb.cryostat.service.Cryostat;

import org.testcontainers.containers.Network;

import com.google.auto.service.AutoService;

@AutoService(Cryostat.class)
public class LocalCryostat extends Cryostat implements ContainerDeployable<CryostatContainer>, WithDockerImage {

    private Network network;
    private CryostatDbContainer dbContainer;
    private CryostatStorageContainer storageContainer;
    private CryostatContainer cryostatContainer;

    @Override
    public void deploy() {
        network = Network.newNetwork();
        dbContainer = new CryostatDbContainer(network);
        dbContainer.start();
        storageContainer = new CryostatStorageContainer(network);
        storageContainer.start();
        cryostatContainer = new CryostatContainer(image(), dbContainer, storageContainer);
        cryostatContainer.start();
    }

    @Override
    public void undeploy() {
        if (cryostatContainer != null) {
            cryostatContainer.stop();
        }
        if (storageContainer != null) {
            storageContainer.stop();
        }
        if (dbContainer != null) {
            dbContainer.stop();
        }
        if (network != null) {
            network.close();
        }
    }

    @Override
    public void openResources() {
        final HTTPUtils client = HTTPUtils.getInstance(HTTPUtils.trustAllSslClient());
        WaitUtils.waitFor(new Waiter(() -> {
            int code = client.get(String.format("%s/health/liveness", connectionUrl()), false).getResponseCode();
            return code == 200 || code == 204;
        }, "wait for container ready"));
        validation().init();
    }

    @Override
    public void closeResources() {
    }

    @Override
    public String connectionUrl() {
        return String.format("http://%s:%d", cryostatContainer.getHost(), CryostatContainer.PORT);
    }

    @Override
    public CryostatClient client() {
        return new LocalCryostatClient(connectionUrl());
    }

    @Override
    public int getPortMapping(int port) {
        return port;
    }

    public String defaultImage() {
        return "registry.redhat.io/cryostat/cryostat-rhel9:latest";
    }

    @Override
    public CryostatContainer container() {
        return cryostatContainer;
    }
}
