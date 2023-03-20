package software.tnb.db.mongodb.validation;

import software.tnb.db.mongodb.account.MongoDBAccount;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import java.util.ArrayList;
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

    public String createDocument(String collectionName, Document document) {
        LOG.info("Publishing document {} into a collection named {}", document.toJson(), collectionName);
        client.getDatabase(account.database()).getCollection(collectionName).insertOne(document);
        // Insert mutates the original object
        return document.get("_id").toString();
    }

    public List<String> createDocuments(String collectionName, Document... documents) {
        List<String> ids = new ArrayList<>();
        for (Document document : documents) {
            ids.add(createDocument(collectionName, document));
        }
        return ids;
    }

    public List<Document> getDocuments(String collectionName) {
        return getDocuments(collectionName, -1);
    }

    public List<Document> getDocuments(String collectionName, int count) {
        return getDocuments(account.database(), collectionName, count);
    }

    private List<Document> getDocuments(String dbName, String collectionName, int count) {
        LOG.debug("Getting documents in MongoDB database {} collection {}", dbName, collectionName);
        MongoCollection<Document> collection = client.getDatabase(dbName).getCollection(collectionName);
        if (count == -1) {
            return StreamSupport.stream(collection.find().spliterator(), false).collect(Collectors.toList());
        } else {
            return StreamSupport.stream(collection.find().spliterator(), false).limit(count).collect(Collectors.toList());
        }
    }

    public UpdateResult replaceDocument(String collectionName, Bson filter, Document document) {
        LOG.info("Replacing at most one document specified by filter {} with new document {} in a collection named {}", filter, document.toJson(),
            collectionName);
        return client.getDatabase(account.database()).getCollection(collectionName).replaceOne(filter, document);
    }

    public UpdateResult updateDocument(String collectionName, Bson filter, Bson document) {
        LOG.info("Updating at most one document specified by filter {} with new document {} in a collection named {}", filter,
            document.toBsonDocument().toJson(), collectionName);
        return client.getDatabase(account.database()).getCollection(collectionName).updateOne(filter, document);
    }

    public DeleteResult deleteDocument(String collectionName, Bson filter) {
        LOG.info("Deleting at most one document by filter {} in a collection named {}", filter, collectionName);
        return client.getDatabase(account.database()).getCollection(collectionName).deleteOne(filter);
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

    public void deleteCollection(String collectionName) {
        client.getDatabase(account.database()).getCollection(collectionName).drop();
    }

    public void copyAllDocuments(String sourceCollection, String destCollection) {
        client.getDatabase(account.database()).getCollection(destCollection).insertMany(getDocuments(sourceCollection));
    }

    public void cloneDatabase(String sourceDatabase) {
        client.getDatabase(sourceDatabase).listCollectionNames().forEach(c -> {
            copyCollectionFromDatabase(sourceDatabase, c);
        });
    }

    public void copyCollectionFromDatabase(String sourceDatabase, String collectionName) {
        CreateCollectionOptions options = new CreateCollectionOptions();
        options.capped(true);
        options.sizeInBytes(1024 * 1024);

        client.getDatabase(account.database()).createCollection(collectionName, options);

        client.getDatabase(account.database()).getCollection(collectionName)
            .insertMany(getDocuments(sourceDatabase, collectionName, -1));
    }

    public void dropDatabase(String databaseName) {
        client.getDatabase(databaseName).drop();
    }
}
