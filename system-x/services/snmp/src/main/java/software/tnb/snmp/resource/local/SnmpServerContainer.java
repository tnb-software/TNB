package software.tnb.snmp.resource.local;

import software.tnb.snmp.service.SnmpServer;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;

public class SnmpServerContainer extends GenericContainer<SnmpServerContainer> {

    public SnmpServerContainer(String image, Map<String, String> env) {
        super(image);
        withEnv(env);
        withExposedPorts(SnmpServer.SNMPD_LISTENING_PORT, SnmpServer.SNMPTRAPD_LISTENING_PORT);
        waitingFor(Wait.forLogMessage(".*NET-SNMP version 5.9.*", 1));
    }
}
