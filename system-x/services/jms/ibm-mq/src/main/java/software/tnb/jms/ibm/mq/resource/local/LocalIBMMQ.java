package software.tnb.jms.ibm.mq.resource.local;

import software.tnb.common.deployment.Deployable;
import software.tnb.jms.ibm.mq.service.IBMMQ;
import software.tnb.jms.ibm.mq.validation.IBMMQValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.images.builder.Transferable;

import com.google.auto.service.AutoService;

import java.io.IOException;

@AutoService(IBMMQ.class)
public class LocalIBMMQ extends IBMMQ implements Deployable {
    private static final Logger LOG = LoggerFactory.getLogger(LocalIBMMQ.class);
    private IBMMQContainer container;

    @Override
    public void deploy() {
        LOG.info("Starting IBM MQ container");
        container = new IBMMQContainer(image(), DEFAULT_PORT, containerEnvironment(), mqscConfig()
            , getConfiguration().keyPath(), getConfiguration().certPath());
        container.start();
        generateKeystore();
        LOG.info("IBM MQ container started");
    }

    @Override
    public void undeploy() {
        if (container != null) {
            container.stop();
        }
    }

    @Override
    public String host() {
        return container.getHost();
    }

    @Override
    public int port() {
        return container.getMappedPort(DEFAULT_PORT);
    }

    @Override
    protected String clientHostname() {
        return host();
    }

    @Override
    public IBMMQValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new IBM MQ validation");
            validation = new IBMMQValidation(account(), client(), container);
        }
        return validation;
    }

    protected void generateKeystore() {
        if (getConfiguration().useSSL()) {
            try {
                final String sslFolder = String.format(IBMMQ.SSL_FOLDER, account().queueManager());
                final String kdbFile = sslFolder + "/key";
                final String certLabel = account().queueManager() + ".cert";

                LOG.debug(container.execInContainer("runmqakm", "-keydb", "-create", "-db"
                    , kdbFile + ".kdb", "-pw", account().password()
                    , "-type", "pkcs12", "-expire", "1000", "-stash").toString());

                LOG.debug(container.execInContainer("runmqakm", "-cert", "-add", "-label", certLabel, "-db", kdbFile + ".kdb"
                    , "-stashed", "-trust", "enable", "-file", IBMMQ.KEYS_FOLDER + "/key.crt").toString());

                String cmd = "REFRESH SECURITY(*) TYPE(SSL)\n";
                container.copyFileToContainer(Transferable.of(cmd), "/tmp/ssl.in");
                LOG.debug(container.execInContainer("runmqsc", "-f", "/tmp/ssl.in", account().queueManager()).toString());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
