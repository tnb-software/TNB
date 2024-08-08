package software.tnb.rest.validation;

import software.tnb.common.validation.Validation;

public class RestValidation implements Validation {

    private final String host;
    private final int port;

    public RestValidation(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String openApiJsonUrl() {
        return String.format("http://%s:%s/api/v3/openapi.json", host, port);
    }
}
