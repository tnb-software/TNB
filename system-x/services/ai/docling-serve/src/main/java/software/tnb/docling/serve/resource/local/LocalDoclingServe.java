package software.tnb.docling.serve.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.docling.serve.service.DoclingServe;

import com.google.auto.service.AutoService;

@AutoService(DoclingServe.class)
public class LocalDoclingServe extends DoclingServe implements ContainerDeployable<DoclingServeContainer> {
    private final DoclingServeContainer container = new DoclingServeContainer(defaultImage(), PORT);

    public String url() {
        return String.format("http://%s:%d", container.getHost(), container.getMappedPort(PORT));
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public DoclingServeContainer container() {
        return container;
    }
}
