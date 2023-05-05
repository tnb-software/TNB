package software.tnb.jms.ibm.mq.service;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.jms.ibm.mq.account.IBMMQAccount;
import software.tnb.jms.ibm.mq.validation.IBMMQValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;

import javax.jms.Connection;
import javax.jms.JMSException;

import java.io.File;
import java.util.Map;

public abstract class IBMMQ extends Service<IBMMQAccount, Connection, IBMMQValidation> implements WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(IBMMQ.class);

    public static final int DEFAULT_PORT = 1414;
    public static final String MQSC_COMMAND_FILES_LOCATION = "/etc/mqm";
    public static final String MQSC_COMMAND_FILE_NAME = "99-tnb.mqsc";

    public IBMMQValidation validation() {
        if (validation == null) {
            LOG.debug("Creating new IBM MQ validation");
            validation = new IBMMQValidation(account(), hostname(), port(), client());
        }
        return validation;
    }

    public Map<String, String> containerEnvironment() {
        return Map.of(
            "LICENSE", "accept",
            "MQ_QMGR_NAME", account().queueManager(),
            "MQ_APP_PASSWORD", account().password()
        );
    }

    public abstract String hostname();

    public abstract int port();

    public String defaultImage() {
        return "icr.io/ibm-messaging/mq:9.2.5.0-r1";
    }

    /**
     * The user has only access to only pre-defined topics and queues. This adds permissions to user defined in the account to access all topics
     * and queues and it is later mounted to the container as a file that is executed by the startup script in the mq image.
     *
     * @return mqsc configuration string
     */
    public String mqscConfig() {
        return "SET AUTHREC PROFILE('*') PRINCIPAL('" + account().username() + "') OBJTYPE(TOPIC) AUTHADD(ALL)\n"
            + "SET AUTHREC PROFILE('*') PRINCIPAL('" + account().username() + "') OBJTYPE(QUEUE) AUTHADD(ALL)\n";
    }

    public void openResources() {
        try {
            // IBM MQ creates a log file by default, so redirect it to target
            System.setProperty("com.ibm.msg.client.commonservices.log.outputName", new File("target/ibmmq.log").getAbsolutePath());
            MQConnectionFactory connectionFactory = new MQConnectionFactory();
            connectionFactory.setHostName(hostname());
            connectionFactory.setPort(port());
            connectionFactory.setChannel(account().channel());
            connectionFactory.setQueueManager(account().queueManager());
            connectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
            client = connectionFactory.createConnection(account().username(), account().password());
            client.start();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create MQConnectionFactory:", e);
        }
    }

    public void closeResources() {
        validation = null;
        if (client != null) {
            try {
                client.stop();
            } catch (JMSException e) {
                LOG.warn("Unable to close connection:", e);
            }
        }
    }

    @Override
    protected Connection client() {
        return client;
    }
}
