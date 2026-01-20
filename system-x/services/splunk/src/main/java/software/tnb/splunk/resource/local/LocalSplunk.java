package software.tnb.splunk.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.splunk.service.Splunk;
import software.tnb.splunk.service.configuration.SplunkProtocol;

import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoService(Splunk.class)
public class LocalSplunk extends Splunk implements ContainerDeployable<SplunkContainer> {
    private SplunkContainer container;

    @Override
    public void defaultConfiguration() {
        getConfiguration().protocol(SplunkProtocol.HTTP).hecEnabled(false);
    }

    @Override
    public SplunkContainer container() {
        return container;
    }

    @Override
    public void deploy() {
        if (SplunkProtocol.HTTPS == getConfiguration().getProtocol()) {
            throw new IllegalStateException("HTTPS protocol is not implemented for Local Splunk service! Use HTTP.");
        }
        Map<String, String> envs = containerEnvironment();
        List<Integer> ports = new ArrayList<>();
        ports.add(UI_PORT);
        ports.add(PORT);
        if (getConfiguration().isHecEnabled()) {
            envs.put("SPLUNK_HEC_TOKEN", account().hecToken());
            ports.add(HEC_PORT);
        }
        container = new SplunkContainer(image(), ports, envs);

        ContainerDeployable.super.deploy();
    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(PORT);
    }

    @Override
    public int hecPort() {
        return container.getMappedPort(HEC_PORT);
    }

    public Map<String, String> containerEnvironment() {
        return new HashMap<>(Map.of(
            "SPLUNK_START_ARGS", "--accept-license",
            "SPLUNKD_SSL_ENABLE", "false",
            "SPLUNK_PASSWORD", account().password()
        ));
    }
}
