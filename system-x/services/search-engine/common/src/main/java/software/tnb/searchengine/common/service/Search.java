package software.tnb.searchengine.common.service;

import software.tnb.common.account.AccountFactory;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.deployment.WithExternalHostname;
import software.tnb.common.deployment.WithName;
import software.tnb.common.service.Service;
import software.tnb.common.util.ReflectionUtil;
import software.tnb.common.utils.HTTPUtils;
import software.tnb.searchengine.common.account.SearchAccount;
import software.tnb.searchengine.common.validation.SearchValidation;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

public abstract class Search<C> extends Service<SearchAccount, C, SearchValidation> implements WithName, WithExternalHostname, WithDockerImage {
    private static final Logger LOG = LoggerFactory.getLogger(Search.class);
    protected static final int PORT = 9200;

    public SearchValidation validation() {
        return validation;
    }

    protected Class<SearchAccount> accountClass() {
        return SearchAccount.class;
    }

    @Override
    public SearchAccount account() {
        if (account == null) {
            account = AccountFactory.create(accountClass());
        }
        return account;
    }

    public abstract C client();

    public abstract Closeable transport();

    public void openResources() {
        account = account();
        client = client();
    }

    public void closeResources() {
        validation = null;
        if (client != null) {
            try {
                transport().close();
            } catch (IOException e) {
                LOG.warn("Unable to close the client: ", e);
            }
        }
    }

    public static String version() {
        return "latest";
    }

    public HttpAsyncClientBuilder httpClientBuilder(HttpAsyncClientBuilder self) {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(account.user(), account.password()));

        return self
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
            .setSSLContext(HTTPUtils.getSslContext())
            .setDefaultCredentialsProvider(credentialsProvider);

    }

    public HttpHost httpHost() {
        return HttpHost.create(url());
    }

    public abstract String clusterName();

    public abstract String host();

    public int port() {
        return PORT;
    }

    @Override
    public String externalHostname() {
        return "localhost";
    }

    public String name() {
        return ReflectionUtil.getSuperClassName(this.getClass());
    }

    public String url() {
        return host() + ":" + port();
    }
}
