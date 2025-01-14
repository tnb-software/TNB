package software.tnb.aws.s3.service.local.ceph;

import software.tnb.aws.s3.service.Ceph;
import software.tnb.aws.s3.validation.S3Validation;
import software.tnb.common.deployment.Deployable;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

@AutoService(Ceph.class)
public class LocalCeph extends Ceph implements Deployable {
    private CephContainer container;

    @Override
    public String hostname() {
        return "http://" + container.getHost() + ":" + container.getMappedPort(CONTAINER_PORT);
    }

    @Override
    public String clientHostname() {
        return hostname();
    }

    @Override
    public void deploy() {
        LOG.info("Starting Ceph container");
        container = new CephContainer(image(), CONTAINER_PORT, environment());
        container.start();
        LOG.info("Ceph container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Ceph container");
            container.stop();
        }
    }

    @Override
    public void openResources() {
        validation = new S3Validation(client());
    }

    @Override
    public void closeResources() {
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        Deployable.super.beforeAll(extensionContext);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        super.afterAll(extensionContext);
        undeploy();
    }
}
