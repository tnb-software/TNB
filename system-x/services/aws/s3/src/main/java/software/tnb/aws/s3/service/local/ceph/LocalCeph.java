package software.tnb.aws.s3.service.local.ceph;

import software.tnb.aws.s3.service.Ceph;
import software.tnb.aws.s3.validation.S3Validation;
import software.tnb.common.deployment.ContainerDeployable;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

@AutoService(Ceph.class)
public class LocalCeph extends Ceph implements ContainerDeployable<CephContainer> {
    private final CephContainer container = new CephContainer(image(), CONTAINER_PORT, environment());

    @Override
    public String hostname() {
        return "http://" + container.getHost() + ":" + container.getMappedPort(CONTAINER_PORT);
    }

    @Override
    public String clientHostname() {
        return hostname();
    }

    @Override
    public void openResources() {
        validation = new S3Validation(client());
    }

    @Override
    public void closeResources() {
    }

    // because Ceph extends AWS service that by default deploys nothing, we need to override the before and afterall methods to the correct ones
    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        ContainerDeployable.super.beforeAll(extensionContext);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        super.afterAll(extensionContext);
        undeploy();
    }

    @Override
    public CephContainer container() {
        return container;
    }
}
