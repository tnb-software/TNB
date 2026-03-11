package software.tnb.jms.ibm.mq.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.ConfigurableService;
import software.tnb.jms.ibm.mq.account.IBMMQAccount;
import software.tnb.jms.ibm.mq.service.configuration.IBMMQConfiguration;
import software.tnb.jms.ibm.mq.validation.IBMMQValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;

import java.io.File;
import java.util.Map;

import jakarta.jms.Connection;
import jakarta.jms.JMSException;

public abstract class IBMMQ extends ConfigurableService<IBMMQAccount, Connection, IBMMQValidation, IBMMQConfiguration> implements WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(IBMMQ.class);

    protected static final int DEFAULT_PORT = 1414;
    public static final String MQSC_COMMAND_FILES_LOCATION = "/etc/mqm";
    public static final String KEYS_FOLDER = "/etc/mqm/pki/keys/mykey";
    public static final String SSL_FOLDER = "/mnt/mqm/data/qmgrs/%s/ssl";
    public static final String MQSC_COMMAND_FILE_NAME = "99-tnb.mqsc";
    public static final String SSLCIPHERSUITE = "TLS_RSA_WITH_AES_128_CBC_SHA256";

    public Map<String, String> containerEnvironment() {
        return Map.of(
            "LICENSE", "accept",
            "MQ_QMGR_NAME", account().queueManager(),
            "MQ_APP_PASSWORD", account().password(),
            "AMQ_SSL_WEAK_CIPHER_ENABLE", SSLCIPHERSUITE
        );
    }

    public abstract String host();

    protected int clientPort() {
        return port();
    }

    // In 9.4 the environment variable for password is deprecated, so for the next bump it will be needed to migrate to docker secrets
    public String defaultImage() {
        return "icr.io/ibm-messaging/mq:9.4.5.0-r1";
    }

    /**
     * The user has only access to only pre-defined topics and queues. This adds permissions to user defined in the account to access all topics
     * and queues and it is later mounted to the container as a file that is executed by the startup script in the mq image.
     *
     * @return mqsc configuration string
     */
    public String mqscConfig() {
        return "SET AUTHREC PROFILE('*') PRINCIPAL('" + account().username() + "') OBJTYPE(TOPIC) AUTHADD(ALL)\n"
            + "SET AUTHREC PROFILE('*') PRINCIPAL('" + account().username() + "') OBJTYPE(QUEUE) AUTHADD(ALL)\n"
            + "SET AUTHREC PROFILE('SYSTEM.DEFAULT.MODEL.QUEUE') OBJTYPE(QUEUE) PRINCIPAL('" + account().username() + "') AUTHADD(ALL)\n";
    }

    public void openResources() {
        try {
            // IBM MQ creates a log file by default, so redirect it to target
            System.setProperty("com.ibm.msg.client.commonservices.log.outputName", new File("target/ibmmq.log").getAbsolutePath());
            MQConnectionFactory connectionFactory = new MQConnectionFactory();
            connectionFactory.setHostName(clientHostname());
            connectionFactory.setPort(clientPort());
            connectionFactory.setChannel(account().channel());
            connectionFactory.setQueueManager(account().queueManager());
            connectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
            if (getConfiguration().useSSL()) {
                connectionFactory.setSSLCipherSuite(SSLCIPHERSUITE);
            }
            client = connectionFactory.createConnection(account().username(), account().password());
            client.start();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create MQConnectionFactory:", e);
        }
    }

    // For openshift, return localhost, as the client is connected via port-forward
    // For local, return "hostname()", as that may be different than "localhost" when running testcontainers on a different host
    protected abstract String clientHostname();

    public void closeResources() {
        if (validation != null) {
            validation.close();
            validation = null;
        }
        if (client != null) {
            try {
                client.stop();
            } catch (JMSException e) {
                LOG.warn("Unable to close connection:", e);
            }
        }
    }

    public int port() {
        return DEFAULT_PORT;
    }

    @Override
    protected void defaultConfiguration() {
        getConfiguration().useSSL(false);
    }
}
