package software.tnb.opensearch.service;

import software.tnb.opensearch.validation.OpensearchValidation;
import software.tnb.searchengine.common.account.SearchAccount;
import software.tnb.searchengine.common.service.Search;
import software.tnb.searchengine.common.validation.SearchValidation;

import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.testcontainers.utility.Base58;

import java.io.Closeable;
import java.util.Map;
import org.apache.commons.lang3.SystemUtils;

public abstract class Opensearch extends Search<OpenSearchClient> {
    private static final String CLUSTER_NAME = "tnb-os";
    private static final String OPENSEARCH_VERSION = "opensearch.version";

    @Override
    public OpenSearchClient client() {

        final RestClient restClient = RestClient
            .builder(httpHost())
            .setHttpClientConfigCallback(this::httpClientBuilder)
            .build();

        return new OpenSearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));
    }

    @Override
    public SearchAccount account() {
        account = new SearchAccount("admin", "RHCamelTest!1234");
        return account;
    }

    @Override
    public Closeable transport() {
        return client._transport();
    }

    @Override
    public SearchValidation validation() {
        if (validation == null) {
            validation = new OpensearchValidation(client());
        }
        return validation;
    }

    @Override
    public String defaultImage() {
        //return "quay.io/fuse_qe/opensearch:" + version();
        if ("ppc64le".equals(SystemUtils.OS_ARCH)) {
            return "icr.io/ppc64le-oss/opensearch-ppc64le:2.12.0";
        }else{
            return "quay.io/fuse_qe/opensearch:" + version();
        }
    }

    public static String version() {
        return System.getProperty(OPENSEARCH_VERSION, "latest");
    }

    @Override
    public String clusterName() {
        return CLUSTER_NAME;
    }

    public abstract String host();

    public String getNetworkAliases() {
        return "opensearch-" + Base58.randomString(6);
    }

    public String containerStartRegex() {
        return ".*Node started.*";
        //return ".*ML configuration initialized successfully.*";
    }

    public Map<String, String> containerEnv() {
        return Map.of(
            "OPENSEARCH_INITIAL_ADMIN_PASSWORD", account().password(),
            "plugins.security.disabled", "true",
            "discovery.type", "single-node");
    }
}
