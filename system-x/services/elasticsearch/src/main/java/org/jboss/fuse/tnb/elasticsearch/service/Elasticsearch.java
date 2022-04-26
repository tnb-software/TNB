package org.jboss.fuse.tnb.elasticsearch.service;

import static org.junit.jupiter.api.Assertions.fail;

import org.jboss.fuse.tnb.common.service.Service;
import org.jboss.fuse.tnb.elasticsearch.account.ElasticsearchAccount;
import org.jboss.fuse.tnb.elasticsearch.validation.ElasticsearchValidation;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public abstract class Elasticsearch implements Service {
    private static final Logger LOG = LoggerFactory.getLogger(Elasticsearch.class);
    private static final String CLUSTER_NAME = "tnb-es";
    private static final String ELASTICSEARCH_VERSION = "elasticsearch.version";

    protected ElasticsearchAccount account;
    protected RestHighLevelClient client;
    private ElasticsearchValidation validation;

    protected RestHighLevelClient client() {
        SSLContext sc = null;
        try {
            // Ignore SSL stuff
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            fail("Error while constructing SSLContext: ", e);
        }
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(account().user(), account().password()));

        SSLContext finalSc = sc;
        return new RestHighLevelClient(RestClient
            .builder(HttpHost.create(clientHost()))
            .setHttpClientConfigCallback(config ->
                config
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .setSSLContext(finalSc)
                    .setDefaultCredentialsProvider(credentialsProvider)
            )
        );
    }

    public ElasticsearchValidation validation() {
        if (validation == null) {
            validation = new ElasticsearchValidation(client);
        }
        return validation;
    }

    public void openResources() {
        client = client();
    }

    public void closeResources() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                LOG.warn("Unable to close elasticsearch client: ", e);
            }
        }
    }

    public String version() {
        return System.getProperty(ELASTICSEARCH_VERSION, "7.13.3");
    }

    public String clusterName() {
        return CLUSTER_NAME;
    }

    public abstract ElasticsearchAccount account();

    protected abstract String clientHost();

    public abstract String host();
}
