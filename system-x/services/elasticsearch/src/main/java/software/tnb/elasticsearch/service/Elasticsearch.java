package software.tnb.elasticsearch.service;

import software.tnb.common.service.Service;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.elasticsearch.account.ElasticsearchAccount;
import software.tnb.elasticsearch.validation.ElasticsearchValidation;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

public abstract class Elasticsearch extends Service<ElasticsearchAccount, ElasticsearchClient, ElasticsearchValidation> {
    private static final Logger LOG = LoggerFactory.getLogger(Elasticsearch.class);
    private static final String CLUSTER_NAME = "tnb-es";
    private static final String ELASTICSEARCH_VERSION = "elasticsearch.version";

    protected ElasticsearchClient client() {

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(account().user(), account().password()));

        RestClient httpClient = RestClient
                .builder(HttpHost.create(clientHost()))
                .setHttpClientConfigCallback(config -> config
                        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                        .setSSLContext(HTTPUtils.getSslContext())
                        .setDefaultCredentialsProvider(credentialsProvider))
                .build();

        ElasticsearchTransport transport = new RestClientTransport(
                httpClient,
                new JacksonJsonpMapper());

        return new ElasticsearchClient(transport);
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
        validation = null;
        if (client != null) {
            try {
                client._transport().close();
            } catch (IOException e) {
                LOG.warn("Unable to close elasticsearch client: ", e);
            }
        }
    }

    public String version() {
        return System.getProperty(ELASTICSEARCH_VERSION, "7.17.9");
    }

    public String clusterName() {
        return CLUSTER_NAME;
    }

    public abstract ElasticsearchAccount account();

    protected abstract String clientHost();

    public abstract String host();
}
