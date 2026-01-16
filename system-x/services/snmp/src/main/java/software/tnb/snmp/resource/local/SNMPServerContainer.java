package software.tnb.snmp.resource.local;

import software.tnb.snmp.service.SNMPServer;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class SNMPServerContainer extends GenericContainer<SNMPServerContainer> {

    public SNMPServerContainer(String image, Map<String, String> env) {
        super(image);
        withEnv(env);
        withExposedPorts(SNMPServer.SNMPD_LISTENING_PORT, SNMPServer.SNMPTRAPD_LISTENING_PORT);
        waitingFor(Wait.forLogMessage(".*NET-SNMP version.*", 1));
    }
}
