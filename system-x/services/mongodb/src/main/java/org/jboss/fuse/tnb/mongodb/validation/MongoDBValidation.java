package org.jboss.fuse.tnb.mongodb.validation;

import org.bson.Document;
import org.jboss.fuse.tnb.mongodb.account.MongoDBAccount;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MongoDBValidation {
    private final MongoClient client;
    private final MongoDBAccount account;

    public MongoDBValidation(MongoClient client, MongoDBAccount account) {
        this.client = client;
        this.account = account;
    }

    public void publish(String collectionName, String message, int count) {
        MongoDatabase database = client.getDatabase(account.database());

        /*
         The consume operation needs taliable cursors which require capped
         collections
         */
        CreateCollectionOptions options = new CreateCollectionOptions();
        options.capped(true);
        options.sizeInBytes(1024 * 1024);

        database.createCollection(collectionName, options);

        MongoCollection<Document> collection = database.getCollection(collectionName);

        List<Document> documents = new ArrayList<>(10);
        for (int i = 0; i < count; i++) {
            Document doc = new Document();

            doc.append("name", "test");
            doc.append("value", message + " at " + new Date());

            documents.add(doc);
        }

        collection.insertMany(documents);
    }

    public List<Document> getDocuments(String collectionName) {
        MongoCollection<Document> collection = client.getDatabase(account.database()).getCollection(collectionName);
        return StreamSupport.stream(collection.find().spliterator(), false).collect(Collectors.toList());
    }
}
