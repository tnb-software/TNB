package software.tnb.jms.ibm.mq.validation;

import software.tnb.common.validation.Validation;
import software.tnb.jms.client.JMSClientManager;
import software.tnb.jms.client.JMSQueueClient;
import software.tnb.jms.client.JMSTopicClient;
import software.tnb.jms.ibm.mq.account.IBMMQAccount;

import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFMessage;
import com.ibm.mq.headers.pcf.PCFMessageAgent;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import jakarta.jms.Connection;

public class IBMMQValidation implements Validation {
    private final IBMMQAccount account;

    private JMSClientManager client;
    private final Connection connection;
    private final PCFMessageAgent agent;

    private final Set<String> createdQueues = new HashSet<>();
    private final Set<String> createdTopics = new HashSet<>();

    public IBMMQValidation(IBMMQAccount account, String host, int port, Connection connection) {
        this.account = account;
        this.connection = connection;
        agent = createPCFAgent(host, port);
    }

    private MQQueueManager createQueueManager(String host, int port) {
        Hashtable<String, Object> properties = new Hashtable<>();
        properties.put(MQConstants.HOST_NAME_PROPERTY, host);
        properties.put(MQConstants.PORT_PROPERTY, port);
        properties.put(MQConstants.CHANNEL_PROPERTY, account.adminChannel());
        properties.put(MQConstants.USE_MQCSP_AUTHENTICATION_PROPERTY, true);
        properties.put(MQConstants.USER_ID_PROPERTY, account.adminUsername());
        properties.put(MQConstants.PASSWORD_PROPERTY, account.adminPassword());
        try {
            return new MQQueueManager(account.queueManager(), properties);
        } catch (MQException e) {
            throw new RuntimeException("Unable to create MQQueueManager:", e);
        }
    }

    private PCFMessageAgent createPCFAgent(String host, int port) {
        try {
            return new PCFMessageAgent(createQueueManager(host, port));
        } catch (MQDataException e) {
            throw new RuntimeException("Unable to create PCFMessageAgent:", e);
        }
    }

    private void sendRequest(PCFMessage request) {
        try {
            agent.send(request);
        } catch (Exception e) {
            throw new RuntimeException("Unable to send PCFMessage:", e);
        }
    }

    private JMSClientManager client() {
        if (client == null) {
            client = new JMSClientManager(connection);
        }
        return client;
    }

    private void createQueue(String queueName) {
        if (!createdQueues.contains(queueName)) {
            PCFMessage request = new PCFMessage(MQConstants.MQCMD_CREATE_Q);
            request.addParameter(MQConstants.MQCA_Q_NAME, queueName);
            request.addParameter(MQConstants.MQIA_Q_TYPE, MQConstants.MQQT_LOCAL);
            sendRequest(request);
            createdQueues.add(queueName);
        }
    }

    private void createTopic(String topicName) {
        if (!createdTopics.contains(topicName)) {
            PCFMessage request = new PCFMessage(MQConstants.MQCMD_CREATE_TOPIC);
            request.addParameter(MQConstants.MQCA_TOPIC_NAME, topicName);
            request.addParameter(MQConstants.MQCA_TOPIC_STRING, topicName);
            request.addParameter(MQConstants.MQIA_TOPIC_TYPE, MQConstants.MQTOPT_LOCAL);
            sendRequest(request);
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
}
