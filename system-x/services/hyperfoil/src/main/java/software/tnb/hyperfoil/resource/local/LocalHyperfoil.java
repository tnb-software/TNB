package software.tnb.hyperfoil.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.hyperfoil.service.Hyperfoil;

import com.google.auto.service.AutoService;

@AutoService(Hyperfoil.class)
public class LocalHyperfoil extends Hyperfoil implements ContainerDeployable<HyperfoilContainer>, WithDockerImage {
    private final HyperfoilContainer container = new HyperfoilContainer(image());

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public String hyperfoilUrl() {
        return container.getHost();
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

    @Override
    public HyperfoilContainer container() {
        return container;
    }
}
