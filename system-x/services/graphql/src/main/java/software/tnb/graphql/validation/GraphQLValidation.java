package software.tnb.graphql.validation;

import software.tnb.common.utils.HTTPUtils;
import software.tnb.common.validation.Validation;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class GraphQLValidation implements Validation {

    private final String host;
    private final int port;

    public GraphQLValidation(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void createSchema(File schema) {
        HTTPUtils.Response response = HTTPUtils.getInstance(HTTPUtils.trustAllSslClient())
            .post(String.format("http://%s:%s/admin/schema", host, port), RequestBody.create(schema,
                MediaType.parse("application/json")));

        if (!response.getBody().contains("\"code\":\"Success\"")) {
            throw new RuntimeException("Importing GraphQL schema wasn't successful. See error: " + response.getBody());
        }
    }
}
