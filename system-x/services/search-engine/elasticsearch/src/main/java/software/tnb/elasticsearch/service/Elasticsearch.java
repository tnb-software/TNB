package software.tnb.elasticsearch.service;

import software.tnb.elasticsearch.validation.ElasticsearchValidation;
import software.tnb.searchengine.common.account.SearchAccount;
import software.tnb.searchengine.common.service.Search;
import software.tnb.searchengine.common.validation.SearchValidation;

import org.elasticsearch.client.RestClient;
import org.testcontainers.utility.Base58;

import java.io.Closeable;
import java.util.Map;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;

public abstract class Elasticsearch extends Search<ElasticsearchClient> {

    private static final String CLUSTER_NAME = "tnb-es";
    private static final String ELASTICSEARCH_VERSION = "elasticsearch.version";

    @Override
    public ElasticsearchClient client() {

        RestClient httpClient = RestClient
            .builder(httpHost())
            .setHttpClientConfigCallback(this::httpClientBuilder)
            .build();

        return new ElasticsearchClient(new RestClientTransport(httpClient, new JacksonJsonpMapper()));
    }

    @Override
    public SearchAccount account() {
        account = new SearchAccount("elastic", "password");
        return account;
    }

    @Override
    public Closeable transport() {
        return client._transport();
    }

    @Override
    public SearchValidation validation() {
        if (validation == null) {
            validation = new ElasticsearchValidation(client());
        }
        return validation;
    }

    @Override
    public String defaultImage() {
        return "docker.elastic.co/elasticsearch/elasticsearch:" + Elasticsearch.version();
    }

    public static String version() {
        return System.getProperty(ELASTICSEARCH_VERSION, "7.17.9");
    }

    @Override
    public String clusterName() {
        return CLUSTER_NAME;
    }

    public abstract String host();

    public String getNetworkAliases() {
        return "elasticsearch-" + Base58.randomString(6);
    }

    public abstract String containerStartRegex();

    public Map<String, String> containerEnv() {
        return Map.of(
            "ELASTIC_PASSWORD", account().password(),
            "xpack.security.enabled", "true",
            "discovery.type", "single-node");
    }
}
