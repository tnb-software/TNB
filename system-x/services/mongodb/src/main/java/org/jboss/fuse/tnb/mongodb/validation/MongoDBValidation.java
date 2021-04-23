package org.jboss.fuse.tnb.mongodb.validation;

import org.jboss.fuse.tnb.mongodb.account.MongoDBAccount;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateCollectionOptions;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MongoDBValidation {
    private static final Logger LOG = LoggerFactory.getLogger(MongoDBValidation.class);

    private final MongoClient client;
    private final MongoDBAccount account;

    public MongoDBValidation(MongoClient client, MongoDBAccount account) {
        this.client = client;
        this.account = account;
    }

    public void createDocument(String collectionName, Document document) {
        LOG.info("Publishing document {} into a collection named {}", document.toJson(), collectionName);
        client.getDatabase(account.database()).getCollection(collectionName).insertOne(document);
    }

    public List<Document> getDocuments(String collectionName) {
        return getDocuments(collectionName, -1);
    }

    public List<Document> getDocuments(String collectionName, int count) {
        LOG.debug("Getting documents in MongoDB collection {}", collectionName);
        MongoCollection<Document> collection = client.getDatabase(account.database()).getCollection(collectionName);
        if (count == -1) {
            return StreamSupport.stream(collection.find().spliterator(), false).collect(Collectors.toList());
        } else {
            return StreamSupport.stream(collection.find().spliterator(), false).limit(count).collect(Collectors.toList());
        }
    }

    public void createCollection(String collectionName) {
       /*
         The consume operation needs taliable cursors which require capped
         collections
         */
        CreateCollectionOptions options = new CreateCollectionOptions();
        options.capped(true);
        options.sizeInBytes(1024 * 1024);

        client.getDatabase(account.database()).createCollection(collectionName, options);
    }
}
