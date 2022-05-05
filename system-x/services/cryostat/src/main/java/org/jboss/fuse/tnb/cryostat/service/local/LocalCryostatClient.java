package org.jboss.fuse.tnb.cryostat.service.local;

import org.jboss.fuse.tnb.cryostat.generated.recording.Recording;
import org.jboss.fuse.tnb.cryostat.generated.targets.Target;
import org.jboss.fuse.tnb.cryostat.service.BaseCryostatClient;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.shaded.okhttp3.MediaType;
import org.testcontainers.shaded.okhttp3.OkHttpClient;
import org.testcontainers.shaded.okhttp3.Request;
import org.testcontainers.shaded.okhttp3.RequestBody;
import org.testcontainers.shaded.okhttp3.Response;

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

public class LocalCryostatClient extends BaseCryostatClient {

    private static final Logger LOG = LoggerFactory.getLogger(LocalCryostatClient.class);

    private final String connectionUrl;
    private final OkHttpClient apiClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public LocalCryostatClient(String connectionUrl) {
        this.connectionUrl = connectionUrl;
        this.apiClient = new OkHttpClient();
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
        return ((List<Map>) get(apiContextUrl, "unable to retrieve targets: %s %s", new TypeReference<List>() { }))
            .stream().map(m -> {
                try {
                    return mapper.readValue(mapper.writeValueAsString(m), Target.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }).collect(Collectors.toList());
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
            , "annotations.cryostat.POD_NAME", podName
        ), Map.class);
    }

    @Override
    public List<Recording> recordings(String apiContextUrl) throws IOException {
        return get(apiContextUrl, "unable to retrieve recordings: %s %s", new TypeReference<List<Recording>>() { });
    }

    @Override
    public void startRecording(String apiContextUrl, String name, Map<String, String> labels) throws IOException {
        final Response result = post(apiContextUrl, Map.of("recordingName", name
                , "events", "template=" + getJfrTemplate()
                , "metadata", "{\"labels\":" + mapper.writeValueAsString(labels) + "}")
            , Reader.class);
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
            LOG.error("error sending POST to {}: {}", apiContextUrl, resp.body() != null ? resp.body().string() : "no body");
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
