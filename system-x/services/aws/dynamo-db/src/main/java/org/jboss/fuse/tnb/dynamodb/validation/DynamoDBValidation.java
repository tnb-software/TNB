package org.jboss.fuse.tnb.dynamodb.validation;

import org.jboss.fuse.tnb.common.service.Validation;
import org.jboss.fuse.tnb.common.utils.WaitUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.StreamViewType;
import software.amazon.awssdk.services.dynamodb.streams.DynamoDbStreamsClient;

public class DynamoDBValidation implements Validation {
    private static final Logger LOG = LoggerFactory.getLogger(DynamoDBValidation.class);
    private static final String TABLE_NAME_PATTERN = "[a-zA-Z0-9.\\-_]{3,255}";

    private final DynamoDbClient client;
    private final DynamoDbStreamsClient streamsClient;

    public DynamoDBValidation(DynamoDbClient client, DynamoDbStreamsClient streamsClient) {
        this.client = client;
        this.streamsClient = streamsClient;
    }

    public void deleteTable(String tableName) {
        LOG.info("Deleting DynamoDB table {}", tableName);
        ListTablesResponse listTables = client.listTables(ListTablesRequest.builder().build());

        if (listTables.tableNames().contains(tableName)) {
            client.deleteTable(b -> b.tableName(tableName));
        } else {
            LOG.debug("The DynamoDB table {} doesn't exist", tableName);
        }
    }

    public void createTable(String tableName, String primaryKey) {
        LOG.info("Creating DynamoDB table {}", tableName);
        if (!tableName.matches(TABLE_NAME_PATTERN)) {
            throw new IllegalArgumentException("Table name must be between 3 and 255 characters, containing only letters, numbers, underscores (_),"
                + " hyphens (-), and periods (.)");
        }
        client.createTable(b ->
            b.tableName(tableName)
            .provisionedThroughput(ProvisionedThroughput.builder()
                .readCapacityUnits(5L)
                .writeCapacityUnits(5L)
                .build())
            .keySchema(KeySchemaElement.builder().attributeName(primaryKey).keyType(KeyType.HASH).build())
            .attributeDefinitions(AttributeDefinition.builder().attributeType(ScalarAttributeType.S).attributeName(primaryKey).build())
        );
        WaitUtils.waitFor(
            () -> "active".equalsIgnoreCase(client.describeTable(b -> b.tableName(tableName)).table().tableStatusAsString()),
            6, 5000L, "Waiting until the DynamoDB table " + tableName + " is created");
    }

    public String enableDataStream(String tableName) {
        client.updateTable(b -> b.tableName(tableName).streamSpecification(s -> s.streamEnabled(true).streamViewType(StreamViewType.NEW_IMAGE)));
        final String streamArn = client.describeTable(b -> b.tableName(tableName)).table().latestStreamArn();
        WaitUtils.waitFor(
            () -> "enabled".equalsIgnoreCase(streamsClient.describeStream(b -> b.streamArn(streamArn)).streamDescription().streamStatusAsString()),
            6, 5000L, "Waiting until the stream for DynamoDB table " + tableName + " is enabled");
        return streamArn;
    }

    public void insert(String tableName, Map<String, String> record) {
        Map<String, AttributeValue> input =
            record.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> AttributeValue.builder().s(entry.getValue()).build())
            );

        client.putItem(PutItemRequest.builder().item(input).tableName(tableName).build());
        LOG.debug("Created item {} in table {}", record, tableName);
    }

    public Map<String, String> getItem(String tableName, String key, String keyVal) {
        Map<String, AttributeValue> keyToGet = Collections.singletonMap(key, AttributeValue.builder()
            .s(keyVal).build());

        GetItemRequest request = GetItemRequest.builder()
            .key(keyToGet)
            .tableName(tableName)
            .build();

        Map<String, String> returnedItem = client.getItem(request).item().entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> entry.getValue().s()
        ));

        return returnedItem;
    }
}
