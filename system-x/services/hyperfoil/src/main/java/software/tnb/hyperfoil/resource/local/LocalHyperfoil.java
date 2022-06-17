package software.tnb.hyperfoil.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.hyperfoil.service.Hyperfoil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.HashMap;

@AutoService(Hyperfoil.class)
public class LocalHyperfoil extends Hyperfoil implements Deployable, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(LocalHyperfoil.class);
    private HyperfoilContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting Hyperfoil");
        container = new HyperfoilContainer(image(), new HashMap<>());
        container.start();
        LOG.info("Hyperfoil container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Hyperfoil container");
            container.stop();
        }
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public String hyperfoilUrl() {
        return "localhost";
    }

    @Override
    public String connection() {
        return "http://" + hyperfoilUrl() + ":" + getPortMapping(8090) + "/";
    }

    @Override
    public int getPortMapping(int port) {
        return 8090;
    }

    @Override
    public String defaultImage() {
        return "quay.io/hyperfoil/hyperfoil:latest";
    }
}
