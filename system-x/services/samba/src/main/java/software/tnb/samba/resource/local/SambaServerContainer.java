package software.tnb.samba.resource.local;

import org.testcontainers.containers.GenericContainer;

import java.util.Map;

public class SambaServerContainer extends GenericContainer<SambaServerContainer> {

    public SambaServerContainer(String image, Map<String, String> env) {
        super(image);
        withEnv(env);
        withExposedPorts(LocalSambaServer.SAMBA_PORT_DEFAULT);
    }
}
