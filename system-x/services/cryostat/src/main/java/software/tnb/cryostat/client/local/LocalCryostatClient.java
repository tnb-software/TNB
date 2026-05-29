package software.tnb.cryostat.client.local;

import software.tnb.cryostat.client.BaseCryostatClient;
import software.tnb.cryostat.generated.recording.Recording;
import software.tnb.cryostat.generated.targets.Target;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LocalCryostatClient extends BaseCryostatClient {

    private static final Logger LOG = LoggerFactory.getLogger(LocalCryostatClient.class);

    private final String connectionUrl;
    private final OkHttpClient apiClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public LocalCryostatClient(String connectionUrl) {
        this.connectionUrl = connectionUrl;
        this.apiClient = new OkHttpClient.Builder()
            .connectTimeout(2, java.util.concurrent.TimeUnit.MINUTES)
            .readTimeout(2, java.util.concurrent.TimeUnit.MINUTES)
            .writeTimeout(2, java.util.concurrent.TimeUnit.MINUTES)
            .build();
    }

    @Override
    public void authenticate(String apiContextUrl) throws IOException {
        Request request = getRequestForUrl(apiContextUrl)
            .post(RequestBody.create(null, new byte[0]))
            .build();
        try (Response resp = apiClient.newCall(request).execute()) {
            if (!resp.isSuccessful()) {
                throw new RuntimeException(String.format("unable to connect to Cryostat: %s %s", resp.code(), resp.message()));
            }
        }
    }

    @Override
    public List<Target> targets(String apiContextUrl) throws IOException {
        Request request = getRequestForUrl(apiContextUrl).build();
        try (Response resp = apiClient.newCall(request).execute()) {
            if (!resp.isSuccessful()) {
                throw new RuntimeException(String.format("unable to retrieve targets: %s %s", resp.code(), resp.message()));
            }
            String body = resp.body().string();
            LOG.info("GET {} response: {}", apiContextUrl, body);
            List<Map> rawList = mapper.readValue(body, new TypeReference<List<Map>>() { });
            return rawList.stream().map(m -> {
                try {
                    return mapper.readValue(mapper.writeValueAsString(m), Target.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }).collect(Collectors.toList());
        }
    }

    @Override
    public void addTarget(String apiContextUrl, String alias, String appName) throws IOException {
        String ip = getIp(appName);
        String port = getPort();
        String connectUrl = String.format("service:jmx:rmi:///jndi/rmi://%s:%s/jmxrmi", ip, port);
        LOG.info("Adding Cryostat target: alias={}, connectUrl={}", alias, connectUrl);
        String json = mapper.writeValueAsString(Map.of("connectUrl", connectUrl, "alias", alias));
        post(apiContextUrl, "application/json", json.getBytes(StandardCharsets.UTF_8), Map.class);
    }

    @Override
    public List<Recording> recordings(String apiContextUrl) throws IOException {
        Request request = getRequestForUrl(apiContextUrl).build();
        try (Response resp = apiClient.newCall(request).execute()) {
            if (!resp.isSuccessful()) {
                throw new RuntimeException(String.format("unable to retrieve recordings: %s %s", resp.code(), resp.message()));
            }
            String body = resp.body().string();
            return mapper.readValue(body, new TypeReference<List<Recording>>() { });
        }
    }

    @Override
    public void startRecording(String apiContextUrl, String name, Map<String, String> labels) throws IOException {
        LOG.info("Starting recording: url={}, name={}, template={}", apiContextUrl, name, getJfrTemplate());
        final Response result = post(apiContextUrl, Map.of("recordingName", name
                , "events", "template=" + getJfrTemplate() + ",type=TARGET"
                , "metadata", "{\"labels\":" + mapper.writeValueAsString(labels) + "}")
            , Reader.class);
        LOG.info("Start recording response: HTTP {}", result.code());
        if (result.code() != 201) {
            throw new IOException("Unable to start recording :" + result.code());
        }
    }

    @Override
    public void stopRecording(String apiContextUrl) throws IOException {
        final Request req = getRequestForUrl(apiContextUrl).patch(RequestBody.create(MediaType.parse("text/plain"), "STOP")).build();
        final Response resp = apiClient.newCall(req).execute();
        if (!resp.isSuccessful()) {
            throw new RuntimeException(String.format("error on stopping recording on %s: %s %s", apiContextUrl, resp.code(), resp.message()));
        }
    }

    @Override
    public void downloadRecording(String apiContextUrl, String destinationPath) throws IOException {
        final Request req = getRequestForUrl(apiContextUrl).build();
        final Response resp = apiClient.newCall(req).execute();
        if (!resp.isSuccessful()) {
            throw new RuntimeException(String.format("error on downloading recording at %s: %s %s", apiContextUrl, resp.code(), resp.message()));
        }
        Files.createDirectories(Paths.get(destinationPath).getParent());
        try (FileOutputStream out = new FileOutputStream(destinationPath)) {
            IOUtils.copy(resp.body().byteStream(), out);
        }
    }

    @Override
    public void deleteRecording(String apiContextUrl) throws IOException {
        delete(apiContextUrl);
    }

    @Override
    public String getIp(String appName) {
        return "localhost";
    }

    @Override
    public String getPort() {
        return "9096";
    }

    @Override
    public String getPodName(String appName) {
        return getIp(appName);
    }

    private <T> T get(String apiContextUrl, String msg, TypeReference<T> returnType) throws IOException {
        Request request = getRequestForUrl(apiContextUrl)
            .build();

        try (Response resp = apiClient.newCall(request).execute()) {
            if (!resp.isSuccessful()) {
                throw new RuntimeException(String.format(msg, resp.code(), resp.message()));
            }
            return mapper.readValue(resp.body().source().inputStream(), new TypeReference<T>() { });
        }
    }

    private Request.Builder getRequestForUrl(String apiContextUrl) {
        return new Request.Builder().url(String.format("%s%s", connectionUrl, apiContextUrl));
    }

    private <T> Response post(String apiContextUrl, String contentType, byte[] body, Class<T> returnType) throws IOException {
        final Request req = getRequestForUrl(apiContextUrl).post(RequestBody.create(MediaType.parse(contentType), body)).build();
        return postRequest(apiContextUrl, returnType, req);
    }

    private <T> Response post(String apiContextUrl, Map<String, String> body, Class<T> returnType) throws IOException {
        final Request req = getRequestForUrl(apiContextUrl).post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                body.entrySet()
                    .stream()
                    .map(e -> {
                        try {
                            return URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8.displayName()) + "="
                                + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8.displayName());
                        } catch (UnsupportedEncodingException ex) {
                            throw new RuntimeException(ex);
                        }
                    }).collect(Collectors.joining("&"))
            )).build();
        return postRequest(apiContextUrl, returnType, req);
    }

    private <T> Response postRequest(String apiContextUrl, Class<T> returnType, Request req) throws IOException {
        final Response resp = apiClient.newCall(req).execute();
        if (!resp.isSuccessful()) {
            String body = resp.body() != null ? resp.body().string() : "no body";
            LOG.error("error sending POST to {} (HTTP {}): {}", apiContextUrl, resp.code(), body);
            throw new RuntimeException(String.format("error on posting on %s: %s %s", apiContextUrl, resp.code(), resp.message()));
        }
        return resp;
    }

    private void delete(String apiContextUrl) throws IOException {
        final Request req = getRequestForUrl(apiContextUrl).delete().build();
        final Response resp = apiClient.newCall(req).execute();
        if (!resp.isSuccessful()) {
            throw new RuntimeException(String.format("error deleting %s: %s %s", apiContextUrl, resp.code(), resp.message()));
        }
    }
}
