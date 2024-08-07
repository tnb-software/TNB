package software.tnb.rest.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.rest.service.Rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.List;

@AutoService(Rest.class)
public class LocalRest extends Rest implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalRest.class);

    private RestContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting REST container");
        List<Integer> ports = new ArrayList<>();
        ports.add(PORT);
        container = new RestContainer(image(), ports);
        container.start();
        LOG.info("REST container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping REST container");
            container.stop();
        }
    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(PORT);
    }
}
