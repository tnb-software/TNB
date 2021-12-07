package org.jboss.fuse.tnb.dynamodb.service;

import org.jboss.fuse.tnb.aws.account.AWSAccount;
import org.jboss.fuse.tnb.aws.client.AWSClient;
import org.jboss.fuse.tnb.aws.service.AWSService;
import org.jboss.fuse.tnb.dynamodb.validation.DynamoDBValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.streams.DynamoDbStreamsClient;

@AutoService(DynamoDB.class)
public class DynamoDB extends AWSService<AWSAccount, DynamoDbClient, DynamoDBValidation> {
    private DynamoDbStreamsClient streamsClient;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        LOG.debug("Creating new DynamoDB validation");
        streamsClient = AWSClient.createDefaultClient(account(), DynamoDbStreamsClient.class);
        validation = new DynamoDBValidation(client(DynamoDbClient.class), streamsClient);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        super.afterAll(extensionContext);
       
        if (streamsClient != null) {
            streamsClient.close();
        }
    }
}
