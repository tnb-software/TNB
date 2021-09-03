package org.jboss.fuse.tnb.common.utils;

import java.io.IOException;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public final class HTTPUtils {
    private static final OkHttpClient CLIENT = new OkHttpClient();

    private HTTPUtils() {
    }

    public static Response get(String url) {
        return execute(new Request.Builder().get().url(url).build());
    }

    public static Response post(String url, RequestBody body) {
        return execute(new Request.Builder().post(body).url(url).build());
    }

    public static Response post(String url, RequestBody body, Map<String, String> headers) {
        return execute(new Request.Builder().post(body).url(url).headers(Headers.of(headers)).build());
    }

    public static Response put(String url, RequestBody body, Map<String, String> headers) {
        return execute(new Request.Builder().put(body).url(url).headers(Headers.of(headers)).build());
    }

    public static Response put(String url, RequestBody body) {
        return execute(new Request.Builder().put(body).url(url).build());
    }

    public static void delete(String url) {
        execute(new Request.Builder().url(url).delete().build());
    }

    private static Response execute(Request request) {
        try {
            okhttp3.Response response = CLIENT.newCall(request).execute();
            int responseCode = response.code();
            String responseBody = null;
            if (response.body() != null) {
                responseBody = response.body().string();
                response.body().close();
            }
            return new Response(responseCode, responseBody);
        } catch (IOException e) {
            throw new RuntimeException("Unable to execute request: ", e);
        }
    }

    public static class Response {
        int responseCode;
        String body;

        public Response(int responseCode, String body) {
            this.responseCode = responseCode;
            this.body = body;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public String getBody() {
            return body;
        }
    }
}
