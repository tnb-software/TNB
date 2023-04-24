package software.tnb.aws.dynamodb.service;

import software.tnb.aws.common.account.AWSAccount;
import software.tnb.aws.common.client.AWSClient;
import software.tnb.aws.common.service.AWSService;
import software.tnb.aws.dynamodb.validation.DynamoDBValidation;

import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.auto.service.AutoService;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.streams.DynamoDbStreamsClient;

@AutoService(DynamoDB.class)
public class DynamoDB extends AWSService<AWSAccount, DynamoDbClient, DynamoDBValidation> {
    private DynamoDbStreamsClient streamsClient;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        super.beforeAll(extensionContext);
        LOG.debug("Creating new DynamoDB validation");
        streamsClient = AWSClient.createDefaultClient(account(), DynamoDbStreamsClient.class,
            getConfiguration().isLocalstack() ? localStack.clientUrl() : null);
        validation = new DynamoDBValidation(client(), streamsClient);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        super.afterAll(extensionContext);
       
        if (streamsClient != null) {
            streamsClient.close();
        }
    }
}
