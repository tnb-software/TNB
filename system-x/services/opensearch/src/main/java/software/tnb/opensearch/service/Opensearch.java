package software.tnb.opensearch.service;

import software.tnb.common.service.Service;
import software.tnb.opensearch.account.OpensearchAccount;
import software.tnb.opensearch.validation.OpensearchValidation;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public abstract class Opensearch extends Service<OpensearchAccount, OpenSearchClient, OpensearchValidation> {
    private static final Logger LOG = LoggerFactory.getLogger(Opensearch.class);
    private static final String CLUSTER_NAME = "tnb-os";
    private static final String OPENSEARCH_VERSION = "opensearch.version";

    protected static final int PORT = 9200;

    protected OpenSearchClient client() {
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(account().user(), account().password()));

        final RestClient restClient = RestClient.builder(HttpHost.create(clientUrl()))
            .setHttpClientConfigCallback(httpClientBuilder ->
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
            ).build();

        final OpenSearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        return new OpenSearchClient(transport);
    }

    public OpensearchValidation validation() {
        if (validation == null) {
            validation = new OpensearchValidation(client);
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
                LOG.warn("Unable to close opensearch client: ", e);
            }
        }
    }

    public String version() {
        return System.getProperty(OPENSEARCH_VERSION, "latest");
    }

    public String clusterName() {
        return CLUSTER_NAME;
    }

    protected String clientUrl() {
        return url();
    }

    public abstract String host();

    public int port() {
        return PORT;
    }

    public String url() {
        return host() + ":" + port();
    }

    public String defaultImage() {
        return "quay.io/fuse_qe/opensearch:" + version();
    }

    public Map<String, String> containerEnvs() {
        return Map.of(
            "OPENSEARCH_INITIAL_ADMIN_PASSWORD", account().password(),
            "plugins.security.disabled", "true",
            "discovery.type", "single-node"
        );
    }
}
