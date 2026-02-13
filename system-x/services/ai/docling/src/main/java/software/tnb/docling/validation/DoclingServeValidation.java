package software.tnb.docling.validation;

import software.tnb.common.validation.Validation;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoclingServeValidation implements Validation {

    private static final Logger LOG = LoggerFactory.getLogger(DoclingServeValidation.class);

    private final CloseableHttpClient httpClient;
    private final String baseUrl;

    public DoclingServeValidation(CloseableHttpClient httpClient, String baseUrl) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
    }

    public boolean isHealthy() {
        HttpGet request = new HttpGet(baseUrl + "/health");
        try (ClassicHttpResponse response = httpClient.execute(request, r -> r)) {
            int statusCode = response.getCode();
            LOG.debug("Health check returned status code: {}", statusCode);
            return statusCode == HttpStatus.SC_OK;
        } catch (Exception e) {
            LOG.error("Health check failed", e);
            return false;
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
