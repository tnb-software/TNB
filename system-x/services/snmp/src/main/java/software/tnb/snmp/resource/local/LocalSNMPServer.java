package software.tnb.snmp.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.snmp.service.SNMPServer;

import com.github.dockerjava.api.model.ExposedPort;
import com.google.auto.service.AutoService;

@AutoService(SNMPServer.class)
public class LocalSNMPServer extends SNMPServer implements ContainerDeployable<SNMPServerContainer> {
    private final SNMPServerContainer container = new SNMPServerContainer(image(), containerEnvironment());

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        // hack to retrieve mapped UDP port
        // https://github.com/testcontainers/testcontainers-java/blob/2.0.2/core/src/main/java/org/testcontainers/containers/ContainerState.java#L169
        return Integer.valueOf(container.getContainerInfo().getNetworkSettings().getPorts().getBindings()
            .get(ExposedPort.udp(SNMPServer.SNMPD_LISTENING_PORT))[0].getHostPortSpec());
    }

    @Override
    public String trapHost() {
        return "0.0.0.0";
    }

    @Override
    public int trapPort() {
        return SNMPServer.SNMPTRAPD_LISTENING_PORT;
    }

    @Override
    public SNMPServerContainer container() {
        return container;
    }
}
