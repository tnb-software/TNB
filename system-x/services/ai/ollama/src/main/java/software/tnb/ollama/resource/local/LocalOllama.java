package software.tnb.ollama.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.ollama.service.Ollama;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(Ollama.class)
public class LocalOllama extends Ollama implements ContainerDeployable<OllamaContainer> {
    private static final Logger LOG = LoggerFactory.getLogger(LocalOllama.class);

    private final OllamaContainer container = new OllamaContainer(defaultImage(), PORT);

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(PORT);
    }

    @Override
    public void openResources() {
        client = HttpClients.custom()
            .setDefaultRequestConfig(RequestConfig.custom()
                .setResponseTimeout(Timeout.ofMinutes(10))
                .build())
            .build();
    }

    @Override
    public void closeResources() {
        validation = null;
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                LOG.warn("Unable to close Ollama client", e);
            }
        }
    }

    @Override
    public OllamaContainer container() {
        return container;
    }
}
