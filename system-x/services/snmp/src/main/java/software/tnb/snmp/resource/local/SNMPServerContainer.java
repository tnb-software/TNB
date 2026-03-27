package software.tnb.snmp.resource.local;

import software.tnb.snmp.service.SNMPServer;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.InternetProtocol;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;

import java.util.Map;

/**
 * To being able to run this container on macOS Colima, you need to start colima with `--port-forwarder=grpc`
 * For more details eg. https://github.com/abiosoft/colima/pull/1343
 */
public class SNMPServerContainer extends GenericContainer<SNMPServerContainer> {

    public SNMPServerContainer(String image, Map<String, String> env) {
        super(image);
        withEnv(env);
        withCreateContainerCmdModifier(cmd -> {
            // hack for dynamic UDP port binding
            // https://github.com/testcontainers/testcontainers-java/issues/4667#issuecomment-965474085
            // https://github.com/testcontainers/testcontainers-java/blob/2.0.2/core/src/
            // main/java/org/testcontainers/containers/ContainerDef.java#L71-L87
            ExposedPort snmpdPort = new ExposedPort(SNMPServer.SNMPD_LISTENING_PORT, InternetProtocol.UDP);
            cmd.getHostConfig().withPortBindings(new PortBinding(Ports.Binding.empty(), snmpdPort));
            cmd.withExposedPorts(snmpdPort);
        });
        waitingFor(Wait.forLogMessage(".*NET-SNMP version.*", 1));
    }
}
