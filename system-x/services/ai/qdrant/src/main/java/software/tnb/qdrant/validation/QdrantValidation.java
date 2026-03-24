package software.tnb.qdrant.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class QdrantValidation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(QdrantValidation.class);
    private static final MediaType JSON = MediaType.parse("application/json");

    private final String baseUrl;

    public QdrantValidation(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String createCollection(String name, int vectorSize, String distance) {
        LOG.debug("Creating collection '{}' with vectorSize={} and distance={}", name, vectorSize, distance);
        String body = String.format("{\"vectors\":{\"size\":%d,\"distance\":\"%s\"}}", vectorSize, distance);
        HTTPUtils.Response response = HTTPUtils.getInstance()
            .put(baseUrl + "/collections/" + name, RequestBody.create(body, JSON));
        return response.getBody();
    }

    public String deleteCollection(String name) {
        LOG.debug("Deleting collection '{}'", name);
        HTTPUtils.getInstance().delete(baseUrl + "/collections/" + name);
        return "";
    }

    public String upsert(String collectionName, String pointsJson) {
        LOG.debug("Upserting points into collection '{}'", collectionName);
        String body = String.format("{\"points\":%s}", pointsJson);
        HTTPUtils.Response response = HTTPUtils.getInstance()
            .put(baseUrl + "/collections/" + collectionName + "/points", RequestBody.create(body, JSON));
        return response.getBody();
    }

    public String query(String collectionName, String queryJson) {
        LOG.debug("Querying collection '{}'", collectionName);
        HTTPUtils.Response response = HTTPUtils.getInstance()
            .post(baseUrl + "/collections/" + collectionName + "/points/query", RequestBody.create(queryJson, JSON));
        return response.getBody();
    }
}
