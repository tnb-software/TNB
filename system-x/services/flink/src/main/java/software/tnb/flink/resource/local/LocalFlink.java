package software.tnb.flink.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.flink.service.Flink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(Flink.class)
public class LocalFlink extends Flink implements ContainerDeployable<FlinkJobManagerContainer> {

    private static final Logger LOG = LoggerFactory.getLogger(LocalFlink.class);
    private FlinkTaskManagerContainer flinkTaskManagerContainer;
    private FlinkJobManagerContainer flinkJobManagerContainer;

    @Override
    public FlinkJobManagerContainer container() {
        return flinkJobManagerContainer;
    }

    @Override
    public void deploy() {
        LOG.info("Starting Flink containers");
        flinkJobManagerContainer = new FlinkJobManagerContainer(image(), PORT);
        flinkJobManagerContainer.start();
        flinkTaskManagerContainer = new FlinkTaskManagerContainer(image(), flinkJobManagerContainer);
        flinkTaskManagerContainer.start();
        LOG.info("Flink containers started");
    }

    @Override
    public void undeploy() {
        LOG.info("Stopping Flink containers");
        if (flinkJobManagerContainer != null) {
            flinkJobManagerContainer.stop();
        }

        if (flinkTaskManagerContainer != null) {
            flinkTaskManagerContainer.stop();
        }
        LOG.info("Flink containers stopped");
    }

    @Override
    public String host() {
        return flinkJobManagerContainer.getHost();
    }

    @Override
    public int port() {
        return flinkJobManagerContainer.getMappedPort(PORT);
    }

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
    }

    @Override
    protected void defaultConfiguration() {
    }
}
