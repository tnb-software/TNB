package software.tnb.snmp.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.snmp.service.SNMPServer;

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
        return SNMPServer.SNMPD_LISTENING_PORT;
    }

    @Override
    public String trapHost() {
        return "0.0.0.0";
    }

    @Override
    public int trapPort() {
        return SNMPServer.SNMPD_LISTENING_PORT;
    }

    @Override
    public SNMPServerContainer container() {
        return container;
    }
}
