package software.tnb.aws.s3.service.local.minio;

import software.tnb.aws.s3.service.Minio;
import software.tnb.aws.s3.validation.S3Validation;
import software.tnb.common.deployment.ContainerDeployable;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

@AutoService(Minio.class)
public class LocalMinio extends Minio implements ContainerDeployable<MinioContainer> {
    private final MinioContainer container = new MinioContainer(image(), CONTAINER_API_PORT, containerEnvironment());

    @Override
    public void openResources() {
        validation = new S3Validation(client());
    }

    @Override
    public void closeResources() {
        // nothing to do
    }

    // because Minio extends AWS service that by default deploys nothing, we need to override the before and afterall methods to the correct ones
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
    public String hostname() {
        return String.format("http://%s:%d", container.getHost(), container.getMappedPort(CONTAINER_API_PORT));
    }

    @Override
    protected String clientHostname() {
        return hostname(); //With local minio deployment, S3 client uses the same hostname as a connection for camel
    }

    @Override
    public MinioContainer container() {
        return container;
    }
}
