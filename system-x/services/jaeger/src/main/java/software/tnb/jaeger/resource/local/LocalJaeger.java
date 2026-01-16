package software.tnb.jaeger.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.jaeger.client.JaegerClient;
import software.tnb.jaeger.client.UnauthenticatedJaegerClient;
import software.tnb.jaeger.service.Jaeger;
import software.tnb.jaeger.service.configuration.JaegerConfiguration;

import org.jetbrains.annotations.NotNull;

import com.google.auto.service.AutoService;

@AutoService(Jaeger.class)
public class LocalJaeger extends Jaeger implements ContainerDeployable<JaegerContainer>, WithDockerImage {
    private final JaegerContainer container = new JaegerContainer(image(), env());

    @Override
    public void openResources() {
    }

    @Override
    public void closeResources() {
        validation = null;
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

    @Override
    public JaegerContainer container() {
        return container;
    }
}
