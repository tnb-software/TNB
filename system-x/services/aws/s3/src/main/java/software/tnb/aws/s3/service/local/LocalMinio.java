package software.tnb.aws.s3.service.local;

import software.tnb.aws.s3.service.Minio;
import software.tnb.aws.s3.validation.S3Validation;
import software.tnb.common.deployment.Deployable;

import org.junit.jupiter.api.extension.ExtensionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

import java.util.Map;

@AutoService(Minio.class)
public class LocalMinio extends Minio implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalMinio.class);

    private MinioContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting Minio container");
        container = new MinioContainer(image(), CONTAINER_API_PORT, containerEnvironment());
        container.start();
        LOG.info("Minio container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Minio container");
            container.stop();
        }
    }

    @Override
    public void openResources() {
        validation = new S3Validation(client());
    }

    @Override
    public void closeResources() {
        // nothing to do
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

    @Override
    public String hostname() {
        return String.format("http://%s:%d", container.getHost(), container.getMappedPort(CONTAINER_API_PORT));
    }

    @Override
    protected String clientHostname() {
        return hostname(); //With local minio deployment, S3 client uses the same hostname as a connection for camel
    }

    public Map<String, String> containerEnvironment() {
        return Map.of(
            "MINIO_ROOT_USER", account().accountId(),
            "MINIO_ROOT_PASSWORD", account().secretKey()
        );
    }
}
