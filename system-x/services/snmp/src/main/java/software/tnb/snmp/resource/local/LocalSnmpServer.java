package software.tnb.snmp.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.snmp.service.SnmpServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(SnmpServer.class)
public class LocalSnmpServer extends SnmpServer implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalSnmpServer.class);

    private SnmpServerContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting SNMP Server container");
        container = new SnmpServerContainer(image(), containerEnvironment());
        container.start();
        LOG.info("SNMP Server container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping SNMP Server container");
            container.stop();
        }
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {

    }

    @Override
    public String host() {
        return container.getContainerInfo().getNetworkSettings().getIpAddress();
    }

    @Override
    public int port() {
        return SnmpServer.SNMPD_LISTENING_PORT;
    }

    @Override
    public String trapHost() {
        return "0.0.0.0";
    }

    @Override
    public int trapPort() {
        return SnmpServer.SNMPD_LISTENING_PORT;
    }

    @Override
    public String getLog() {
        return container.getLogs();
    }
}
