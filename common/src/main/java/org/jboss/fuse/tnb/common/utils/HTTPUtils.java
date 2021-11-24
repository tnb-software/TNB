package org.jboss.fuse.tnb.common.utils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public final class HTTPUtils {
    private static HTTPUtils instance;

    private final OkHttpClient client;

    private HTTPUtils(OkHttpClient client) {
        this.client = client;
    }

    public Response get(String url) {
        return execute(new Request.Builder().get().url(url).build());
    }

    public Response get(String url, Map<String, String> headers) {
        return execute(new Request.Builder().get().url(url).headers(Headers.of(headers)).build());
    }

    public Response post(String url, RequestBody body) {
        return execute(new Request.Builder().post(body).url(url).build());
    }

    public Response post(String url, RequestBody body, Map<String, String> headers) {
        return execute(new Request.Builder().post(body).url(url).headers(Headers.of(headers)).build());
    }

    public Response put(String url, RequestBody body, Map<String, String> headers) {
        return execute(new Request.Builder().put(body).url(url).headers(Headers.of(headers)).build());
    }

    public Response put(String url, RequestBody body) {
        return execute(new Request.Builder().put(body).url(url).build());
    }

    public void delete(String url) {
        execute(new Request.Builder().url(url).delete().build());
    }

    public void delete(String url, Map<String, String> headers) {
        execute(new Request.Builder().url(url).delete().headers(Headers.of(headers)).build());
    }

    private Response execute(Request request) {
        try {
            okhttp3.Response response = client.newCall(request).execute();
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

    public static HTTPUtils getInstance() {
        if (instance == null) {
            instance = new HTTPUtils(new OkHttpClient());
        }
        return instance;
    }

    public static HTTPUtils getInstance(OkHttpClient client) {
        return new HTTPUtils(client);
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

    private static final TrustManager[] trustAllCerts = new TrustManager[] {
        new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
        }
    };
    private static final SSLContext trustAllSslContext;

    static {
        try {
            trustAllSslContext = SSLContext.getInstance("SSL");
            trustAllSslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    private static final SSLSocketFactory trustAllSslSocketFactory = trustAllSslContext.getSocketFactory();

    public static OkHttpClient trustAllSslClient() {

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.sslSocketFactory(trustAllSslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        return builder.build();
    }
}
