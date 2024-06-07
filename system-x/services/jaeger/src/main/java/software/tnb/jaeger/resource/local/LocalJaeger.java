package software.tnb.jaeger.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.jaeger.client.JaegerClient;
import software.tnb.jaeger.client.UnauthenticatedJaegerClient;
import software.tnb.jaeger.service.Jaeger;
import software.tnb.jaeger.service.configuration.JaegerConfiguration;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(Jaeger.class)
public class LocalJaeger extends Jaeger implements Deployable, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(LocalJaeger.class);
    private JaegerContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting Jaeger container");
        container = new JaegerContainer(image(), env());
        container.start();
        LOG.info("Jaeger container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Jaeger container");
            container.stop();
        }
    }

    @Override
    public void openResources() {

    }

    @Override
    public void closeResources() {
        validation = null;
    }

    @Override
    public String getLog() {
        return container.getLogs();
    }

    @Override
    public String getCollectorUrl(JaegerConfiguration.CollectorPort port) {
        return getUrl(port);
    }

    @Override
    public String getQueryUrl(JaegerConfiguration.QueryPort port) {
        return getUrl(port);
    }

    @Override
    public String getExternalUrl() {
        return getQueryUrl(JaegerConfiguration.QueryPort.HTTP);
    }

    @Override
    protected JaegerClient client() {
        return new UnauthenticatedJaegerClient(getQueryUrl(JaegerConfiguration.QueryPort.HTTP));
    }

    @NotNull
    private String getUrl(JaegerConfiguration.WithPort port) {
        return String.format("http://%s:%d", container.getHost(), port.portNumber());
    }

    @Override
    public String defaultImage() {
        return "registry.redhat.io/rhosdt/jaeger-all-in-one-rhel8:1.57.0";
    }
}
