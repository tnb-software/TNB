package software.tnb.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

public final class HTTPUtils {

    private static final Logger LOG = LoggerFactory.getLogger(HTTPUtils.class);
    private static HTTPUtils instance;

    private final OkHttpClient client;

    private boolean withRetry = false;
    private List<Integer> retryCodes = List.of(503); //503 OCP route is not ready
    private List<String> retryAllowedMethods = List.of("GET", "POST");

    private HTTPUtils(OkHttpClient client) {
        this.client = client;
    }

    public Response get(String url, boolean throwError) {
        return execute(new Request.Builder().get().url(url).build(), throwError);
    }

    public Response get(String url) {
        return get(url, true);
    }

    public Response get(String url, Map<String, String> headers) {
        return execute(new Request.Builder().get().url(url).headers(Headers.of(headers)).build(), true);
    }

    public Response post(String url, RequestBody body) {
        return execute(new Request.Builder().post(body).url(url).build(), true);
    }

    public Response post(String url, RequestBody body, Map<String, String> headers) {
        return execute(new Request.Builder().post(body).url(url).headers(Headers.of(headers)).build(), true);
    }

    public Response put(String url, RequestBody body, Map<String, String> headers) {
        return execute(new Request.Builder().put(body).url(url).headers(Headers.of(headers)).build(), true);
    }

    public Response put(String url, RequestBody body) {
        return execute(new Request.Builder().put(body).url(url).build(), true);
    }

    public Response patch(String url, RequestBody body, Map<String, String> headers) {
        return execute(new Request.Builder().patch(body).url(url).headers(Headers.of(headers)).build(), true);
    }

    public Response patch(String url, RequestBody body) {
        return execute(new Request.Builder().patch(body).url(url).build(), true);
    }

    public void delete(String url) {
        execute(new Request.Builder().url(url).delete().build(), true);
    }

    public void delete(String url, Map<String, String> headers) {
        execute(new Request.Builder().url(url).delete().headers(Headers.of(headers)).build(), true);
    }

    private Response execute(Request request, boolean throwError, int attempts) {
        try {
            okhttp3.Response response = client.newCall(request).execute();
            String responseBody = null;
            if (response.body() != null) {
                responseBody = response.body().string();
                response.body().close();
            }
            if (withRetry && attempts > 0 && retryAllowedMethods.contains(request.method()) && this.retryCodes.contains(response.code())) {
                LOG.warn("retrying the http call in 1 second");
                WaitUtils.sleep(1000);
                return execute(request, throwError, attempts - 1);
            }
            return new Response(response.code(), responseBody);
        } catch (IOException e) {
            if (throwError) {
                throw new RuntimeException("Unable to execute request: ", e);
            } else {
                LOG.warn("execute error is ignored: {}", e.getMessage());
                return new Response(0, null);
            }
        }
    }

    private Response execute(Request request, boolean throwError) {
        return execute(request, throwError, 5);
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

    public HTTPUtils withRetry(List<Integer> codes, List<String> allowedMethods) {
        this.withRetry = true;
        this.retryCodes = codes;
        this.retryAllowedMethods = allowedMethods;
        return this;
    }

    public HTTPUtils withRetry() {
        return withRetry(this.retryCodes, this.retryAllowedMethods);
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

        public boolean isSuccessful() {
            return responseCode >= 200 && responseCode < 300;
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
    private static final SSLContext sslContext;

    static {
        try {
            if (FIPSUtils.isFipsEnabled()) {
                sslContext = SSLContext.getDefault();
            } else {
                sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            }
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    public static SSLContext getSslContext() {
        return sslContext;
    }

    public static OkHttpClient trustAllSslClient() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
        builder.hostnameVerifier((hostname, session) -> true);
        return builder.build();
    }
    
    public static class OkHttpClientBuilder {
    
        private OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
    
        public OkHttpClientBuilder trustAllSslClient() {
            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            return this;
        }
        
        public OkHttpClientBuilder log() {
            HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
            logger.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logger);
            return this;
        }
        
        public OkHttpClient.Builder getInternalBuilder() {
            return builder;
        }
        
        public OkHttpClient build() {
            return builder.build();
        }
        
    }
}
