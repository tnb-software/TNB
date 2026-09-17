package software.tnb.cyberark.cyberarkvault.resource.local;

import software.tnb.common.deployment.ContainerDeployable;
import software.tnb.common.service.ServiceFactory;
import software.tnb.common.utils.NetworkUtils;
import software.tnb.common.utils.WaitUtils;
import software.tnb.common.utils.waiter.Waiter;
import software.tnb.cyberark.cyberarkvault.service.CyberarkVault;
import software.tnb.db.postgres.service.PostgreSQL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;

import com.google.auto.service.AutoService;

import java.io.IOException;

@AutoService(CyberarkVault.class)
public class LocalCyberarkVault extends CyberarkVault implements ContainerDeployable<CyberarkVaultContainer> {
    private static final Logger LOG = LoggerFactory.getLogger(LocalCyberarkVault.class);

    private final PostgreSQL postgres = ServiceFactory.create(PostgreSQL.class);
    private CyberarkVaultContainer container;
    private int port;

    @Override
    public void deploy() {
        try {
            postgres.beforeAll(null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to deploy the PostgreSQL dependency", e);
        }

        port = NetworkUtils.getFreePort();
        LOG.info("Starting CyberarkVault container on host port {}", port);
        container = new CyberarkVaultContainer(image(), containerEnvironment(databaseUrl(), port));
        container.start();
    }

    @Override
    public void undeploy() {
        if (container != null) {
            container.stop();
        }
        try {
            postgres.afterAll(null);
        } catch (Exception e) {
            LOG.warn("Unable to undeploy the PostgreSQL", e);
        }
        NetworkUtils.releasePort(port);
    }

    @Override
    public void openResources() {
        WaitUtils.waitFor(new Waiter(() -> {
            try {
                return client().get(url() + "/", false).getResponseCode() == 200;
            } catch (Exception e) {
                return false;
            }
        }, "Waiting until the CyberarkVault server is ready"));
        account().setApiKey(createAccount());
    }

    @Override
    public void closeResources() {
        // nothing to close
    }

    private String createAccount() {
        LOG.info("Creating CyberarkVault account '{}'", account().accountName());
        try {
            Container.ExecResult result = container().execInContainer("conjurctl", "account", "create", account().accountName());
            return parseApiKey(result.getStdout());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Unable to create the CyberarkVault account", e);
        }
    }

    private String databaseUrl() {
        return String.format("postgres://%s:%s@localhost:%d/%s",
            postgres.account().username(), postgres.account().password(), postgres.port(), postgres.account().database());
    }

    @Override
    public String url() {
        return String.format("http://%s:%d", container().getHost(), port);
    }

    @Override
    public CyberarkVaultContainer container() {
        return container;
    }
}
