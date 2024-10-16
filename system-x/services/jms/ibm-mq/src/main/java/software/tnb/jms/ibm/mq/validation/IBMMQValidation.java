package software.tnb.jms.ibm.mq.validation;

import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.validation.Validation;
import software.tnb.jms.client.JMSClientManager;
import software.tnb.jms.client.JMSQueueClient;
import software.tnb.jms.client.JMSTopicClient;
import software.tnb.jms.ibm.mq.account.IBMMQAccount;
import software.tnb.jms.ibm.mq.resource.local.IBMMQContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;

import java.util.HashSet;
import java.util.Set;

import cz.xtf.core.openshift.PodShellOutput;
import io.fabric8.kubernetes.api.model.Pod;
import jakarta.jms.Connection;

public class IBMMQValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(IBMMQValidation.class);

    private final IBMMQAccount account;

    private JMSClientManager client;
    private final Connection connection;
    private IBMMQContainer container;
    private Pod pod;

    private final Set<String> createdQueues = new HashSet<>();
    private final Set<String> createdTopics = new HashSet<>();

    public IBMMQValidation(IBMMQAccount account, Connection connection, IBMMQContainer container) {
        this.account = account;
        this.connection = connection;
        this.container = container;
    }

    public IBMMQValidation(IBMMQAccount account, Connection connection, Pod pod) {
        this.account = account;
        this.connection = connection;
        this.pod = pod;
    }

    private JMSClientManager client() {
        if (client == null) {
            client = new JMSClientManager(connection);
        }
        return client;
    }

    private void createQueue(String queueName) {
        if (!createdQueues.contains(queueName)) {
            executeCommand("echo \"DEFINE QLOCAL('" + queueName + "')\" | runmqsc " + account.queueManager());
            createdQueues.add(queueName);
        }
    }

    private void createTopic(String topicName) {
        if (!createdTopics.contains(topicName)) {
            executeCommand("echo \"DEFINE TOPIC('" + topicName + "') TOPICSTR('" + topicName + "') REPLACE\" | runmqsc " + account.queueManager());
            createdTopics.add(topicName);
        }
    }

    public JMSQueueClient queue(String queueName) {
        createQueue(queueName);
        return client().queue(queueName);
    }

    public JMSTopicClient topic(String topicName) {
        createTopic(topicName);
        return client().topic(topicName);
    }

    private void executeCommand(String command) {
        String out;
        String err;
        if (pod != null) {
            final PodShellOutput output = OpenshiftClient.get().podShell(pod).executeWithBash(command);
            out = output.getOutput();
            err = output.getError();
        } else {
            try {
                final Container.ExecResult result = container.execInContainer("/bin/bash", "-c", command);
                out = result.getStdout();
                err = result.getStderr();
            } catch (Exception e) {
                throw new RuntimeException("Unable to execute command in container", e);
            }
        }
        if (!err.isEmpty()) {
            throw new RuntimeException("Unable to execute command: " + command + ": " + err);
        } else {
            LOG.trace(out);
        }
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
