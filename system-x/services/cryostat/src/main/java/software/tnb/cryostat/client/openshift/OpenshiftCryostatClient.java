package software.tnb.cryostat.client.openshift;

import software.tnb.cryostat.generated.targets.Target;
import software.tnb.cryostat.client.BaseCryostatClient;
import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.openshift.OpenshiftClient;
import software.tnb.common.utils.StringUtils;
import software.tnb.cryostat.generated.recording.Recording;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.http.HttpClient;
import io.fabric8.kubernetes.client.http.HttpRequest;
import io.fabric8.kubernetes.client.http.HttpResponse;

public class OpenshiftCryostatClient extends BaseCryostatClient {

    private static final Logger LOG = LoggerFactory.getLogger(OpenshiftCryostatClient.class);

    private final HttpClient apiClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String connectionUrl;

    public OpenshiftCryostatClient(String connectionUrl) {
        this.apiClient = OpenshiftClient.get().authorization().getHttpClient();
        this.connectionUrl = connectionUrl;
    }

    @Override
    public void authenticate(String apiContextUrl) throws IOException {
        post(apiContextUrl, "application/json", new byte[0], Void.class);
    }

    @Override
    public List<Target> targets(String apiContextUrl) throws IOException {
        return mapper.readValue(get(apiContextUrl, "unable to retrieve targets: %s %s").body(), new TypeReference<>() {
        });
    }

    @Override
    public void addTarget(String apiContextUrl, String alias, String appName) throws IOException {
        String ip = getIp(appName);
        String port = getPort();
        String podName = getPodName(appName);
        post(apiContextUrl, Map.of("connectUrl", String.format("service:jmx:rmi:///jndi/rmi://%s:%s/jmxrmi", ip, port)
            , "alias", alias
            , "annotations.cryostat.HOST", ip
            , "annotations.cryostat.PORT", port
            , "annotations.cryostat.NAMESPACE", OpenshiftClient.get().getNamespace()
            , "annotations.cryostat.POD_NAME", podName
        ), Map.class);
    }

    @Override
    public List<Recording> recordings(String apiContextUrl) throws IOException {
        return mapper.readValue(get(apiContextUrl, "unable to retrieve recordings: %s %s").body(), new TypeReference<>() {
        });
    }

    @Override
    public void startRecording(String apiContextUrl, String name, Map<String, String> labels) throws IOException {
        final HttpResponse<Reader> result = post(apiContextUrl, Map.of("recordingName", name
                , "events", "template=" + getJfrTemplate()
                , "metadata", "{\"labels\":" + mapper.writeValueAsString(labels) + "}")
            , Reader.class);
        if (result.code() != 201) {
            throw new IOException("Unable to start recording :" + result.code());
        }
    }

    @Override
    public void stopRecording(String apiContextUrl) throws IOException {
        final HttpRequest req = getRequestForUrl(apiContextUrl).patch("text/plain", "STOP").build();
        final HttpResponse<Void> resp = sendRequest(req, Void.class);
        if (!resp.isSuccessful()) {
            throw new RuntimeException(String.format("error on stopping recording on %s: %s %s", apiContextUrl, resp.code(), resp.message()));
        }
    }

    @Override
    public void downloadRecording(String apiContextUrl, String destinationPath) throws IOException {
        final HttpRequest req = getRequestForUrl(apiContextUrl).build();
        final HttpResponse<InputStream> resp = sendRequest(req, InputStream.class);
        if (!resp.isSuccessful()) {
            throw new RuntimeException(String.format("error on downloading recording at %s: %s %s", apiContextUrl, resp.code(), resp.message()));
        }
        Files.createDirectories(Paths.get(destinationPath).getParent());
        try (FileOutputStream out = new FileOutputStream(destinationPath)) {
            IOUtils.copy(resp.body(), out);
        }
    }

    @Override
    public void deleteRecording(String apiContextUrl) throws IOException {
        delete(apiContextUrl);
    }

    @Override
    public String getIp(String appName) {
        return getPod(appName).getStatus().getPodIP();
    }

    @Override
    public String getPort() {
        return "9096";
    }

    @Override
    public String getPodName(String appName) {
        return getPod(appName).getMetadata().getName();
    }

    private HttpResponse<Reader> get(String apiContextUrl, String format) throws IOException {
        final HttpRequest req = getRequestForUrl(apiContextUrl).build();
        final HttpResponse<Reader> resp = sendRequest(req, Reader.class);
        if (!resp.isSuccessful()) {
            LOG.error("error sending GET to {}: {}", apiContextUrl, resp.bodyString());
            throw new RuntimeException(String.format(format, resp.code(), resp.message()));
        }
        return resp;
    }

    private <T> HttpResponse<T> post(String apiContextUrl, String contentType, byte[] body, Class<T> returnType) throws IOException {
        final HttpRequest req = getRequestForUrl(apiContextUrl).post(contentType, body).build();
        return postRequest(apiContextUrl, returnType, req);
    }

    private <T> HttpResponse<T> post(String apiContextUrl, Map<String, String> body, Class<T> returnType) throws IOException {
        final HttpRequest req = getRequestForUrl(apiContextUrl).post(body).build();
        return postRequest(apiContextUrl, returnType, req);
    }

    private <T> HttpResponse<T> postRequest(String apiContextUrl, Class<T> returnType, HttpRequest req) throws IOException {
        final HttpResponse<T> resp;
        resp = sendRequest(req, returnType);
        if (!resp.isSuccessful()) {
            LOG.error("error sending POST to {}: {}", apiContextUrl, resp.bodyString());
            throw new RuntimeException(String.format("error on posting on %s: %s %s", apiContextUrl, resp.code(), resp.message()));
        }
        return resp;
    }

    private HttpRequest.Builder getRequestForUrl(String apiContextUrl) throws MalformedURLException {
        return apiClient.newHttpRequestBuilder().url(new URL(String.format("%s%s", connectionUrl, apiContextUrl)))
            .header("Authorization", String.format("Bearer %s"
                , StringUtils.base64Encode(OpenshiftClient.get().authorization().getConfiguration().getOauthToken())));
    }

    private Pod getPod(String appName) {
        return Optional.ofNullable(OpenshiftClient.get().getLabeledPods(OpenshiftConfiguration.openshiftDeploymentLabel()
                    , appName)
                .stream().findFirst()
                .orElseGet(() -> OpenshiftClient.get().getAnyPod(appName)))
            .orElseThrow(() -> new IllegalArgumentException("no pod found for integration"));
    }

    private void delete(String apiContextUrl) throws IOException {
        final HttpRequest req = getRequestForUrl(apiContextUrl).delete("text/plain", "").build();
        final HttpResponse<Void> resp = sendRequest(req, Void.class);
        if (!resp.isSuccessful()) {
            throw new RuntimeException(String.format("error deleting %s: %s %s", apiContextUrl, resp.code(), resp.message()));
        }
    }

    private <T> HttpResponse<T> sendRequest(HttpRequest request, Class<T> responseClass) {
        final HttpResponse<T> resp;
        try {
            resp = apiClient.sendAsync(request, responseClass).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Unable to invoke request", e);
        }
        return resp;
    }
}
