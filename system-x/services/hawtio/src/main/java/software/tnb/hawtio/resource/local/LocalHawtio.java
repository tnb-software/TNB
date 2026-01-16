package software.tnb.hawtio.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.hawtio.client.local.LocalHawtioClient;
import software.tnb.hawtio.service.Hawtio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(Hawtio.class)
public class LocalHawtio extends Hawtio implements ContainerDeployable<HawtioContainer>, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(LocalHawtio.class);
    private final int port = NetworkUtils.getFreePort();
    private final HawtioContainer container = new HawtioContainer(image(), port);

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

    @Override
    public HawtioContainer container() {
        return container;
    }
}
