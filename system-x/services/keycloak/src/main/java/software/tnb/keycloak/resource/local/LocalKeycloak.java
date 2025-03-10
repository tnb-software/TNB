package software.tnb.keycloak.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.keycloak.service.Keycloak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auto.service.AutoService;

@AutoService(Keycloak.class)
public class LocalKeycloak extends Keycloak implements Deployable {

    private static final Logger LOG = LoggerFactory.getLogger(LocalKeycloak.class);

    private KeycloakContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting Keycloak container");
        container = new KeycloakContainer(image(), getEnv(), startupParams(), PORT);
        container.start();
        LOG.info("Keycloak container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            LOG.info("Stopping Keycloak container");
            container.stop();
        }
    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return PORT;
    }
}
