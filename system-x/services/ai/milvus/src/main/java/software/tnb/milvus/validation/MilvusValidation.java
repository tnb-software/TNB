package software.tnb.milvus.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MilvusValidation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(MilvusValidation.class);
    private static final MediaType JSON = MediaType.parse("application/json");

    private final String baseUrl;

    public MilvusValidation(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String listCollections() {
        LOG.debug("Listing collections");
        String body = "{}";
        HTTPUtils.Response response = HTTPUtils.getInstance()
            .post(baseUrl + "/v2/vectordb/collections/list", RequestBody.create(body, JSON));
        return response.getBody();
    }

    public String createCollection(String collectionName, int dimension) {
        LOG.debug("Creating collection '{}' with dimension={}", collectionName, dimension);
        String body = String.format("{\"collectionName\":\"%s\",\"dimension\":%d,\"autoId\":true}", collectionName, dimension);
        HTTPUtils.Response response = HTTPUtils.getInstance()
            .post(baseUrl + "/v2/vectordb/collections/create", RequestBody.create(body, JSON));
        return response.getBody();
    }

    public String dropCollection(String collectionName) {
        LOG.debug("Dropping collection '{}'", collectionName);
        String body = String.format("{\"collectionName\":\"%s\"}", collectionName);
        HTTPUtils.Response response = HTTPUtils.getInstance()
            .post(baseUrl + "/v2/vectordb/collections/drop", RequestBody.create(body, JSON));
        return response.getBody();
    }

    public String insert(String collectionName, String dataJson) {
        LOG.debug("Inserting data into collection '{}'", collectionName);
        String body = String.format("{\"collectionName\":\"%s\",\"data\":%s}", collectionName, dataJson);
        HTTPUtils.Response response = HTTPUtils.getInstance()
            .post(baseUrl + "/v2/vectordb/entities/insert", RequestBody.create(body, JSON));
        return response.getBody();
    }

    public String search(String collectionName, String searchDataJson, int limit) {
        LOG.debug("Searching collection '{}' with limit={}", collectionName, limit);
        String body = String.format("{\"collectionName\":\"%s\",\"data\":%s,\"limit\":%d}",
            collectionName, searchDataJson, limit);
        HTTPUtils.Response response = HTTPUtils.getInstance()
            .post(baseUrl + "/v2/vectordb/entities/search", RequestBody.create(body, JSON));
        return response.getBody();
    }
}
