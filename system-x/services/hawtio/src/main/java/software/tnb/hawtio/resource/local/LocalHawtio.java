package software.tnb.hawtio.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.hawtio.client.local.LocalHawtioClient;
import software.tnb.hawtio.service.Hawtio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(Hawtio.class)
public class LocalHawtio extends Hawtio implements Deployable, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(LocalHawtio.class);
    private HawtioContainer container;
    private final int port = NetworkUtils.getFreePort();

    @Override
    public void deploy() {
        LOG.info("Starting Hawtio");
        container = new HawtioContainer(image(), port);
        container.start();
        LOG.info("Hawtio container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Hawtio container");
            container.stop();
        }
    }

    @Override
    public void openResources() {
        this.client = new LocalHawtioClient(getHawtioUrl());
    }

    @Override
    public void closeResources() {
        //do nothing
    }

    public String defaultImage() {
        return "quay.io/rh_integration/hawtio-jbang:4.1.0";
    }

    @Override
    public String getHawtioUrl() {
        return String.format("http://localhost:%s", port);
    }
}
