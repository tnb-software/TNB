package software.tnb.aws.s3.service.local.ceph;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import com.github.dockerjava.api.model.Ulimit;

import java.util.List;
import java.util.Map;

public class CephContainer extends GenericContainer<CephContainer> {
    public CephContainer(String image, int port, Map<String, String> env) {
        super(image);
        this.withEnv(env);
        this.withExposedPorts(port);
        this.withCreateContainerCmdModifier(cmd ->
            // increase the limits to speed up the startup
            cmd.getHostConfig().withUlimits(List.of(new Ulimit("nofile", 65536, 65536))));
        this.waitingFor(Wait.forListeningPorts(port));
    }
}
