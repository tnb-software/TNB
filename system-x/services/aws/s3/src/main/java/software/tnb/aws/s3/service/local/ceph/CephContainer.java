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
        // for rootless podman, it is needed to increase the file size limit, otherwise the container won't start
        // so override the entrypoint to bash and invoke ulimit -f unlimited first
        this.withCommand("-c", "ulimit -f unlimited && /opt/ceph-container/bin/demo");
        this.withCreateContainerCmdModifier(cmd ->
            cmd.withEntrypoint("/bin/bash")
                // increase the limits to speed up the startup (works only on docker)
                .getHostConfig().withUlimits(List.of(new Ulimit("nofile", 65536, 65536))));
        this.waitingFor(Wait.forLogMessage(".*/opt/ceph-container/bin/demo: SUCCESS.*", 1));
    }
}
