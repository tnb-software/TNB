package software.tnb.aws.s3.service.local.minio;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

/**
 * If you want to connect to MinioUI for manually verifying something via container IP, you find UI port in the container log (port is always
 * different)
 */
public class MinioContainer extends GenericContainer<MinioContainer> {
    public MinioContainer(String image, int containerApiPort, Map<String, String> env) {
        super(image);
        this.withEnv(env);
        this.withExposedPorts(containerApiPort);
        this.withCommand("server", "/data");
        this.waitingFor(Wait.forListeningPorts(containerApiPort));
    }
}
