package software.tnb.product.git.support;

import org.eclipse.jgit.transport.http.HttpConnection;
import org.eclipse.jgit.transport.http.HttpConnectionFactory2;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;

public class GitHttpConnectionFactory implements HttpConnectionFactory2 {
    @Override
    public HttpConnection create(URL url) throws IOException {
        return new GitHttpConnection(url);
    }

    @Override
    public HttpConnection create(URL url, Proxy proxy)
        throws IOException {
        return new GitHttpConnection(url, proxy);
    }

    @Override
    public GitSession newSession() {
        return new GitHttpConnectionFactory.JdkConnectionSession();
    }

    private static class JdkConnectionSession implements GitSession {

        private SSLContext securityContext;

        private SSLSocketFactory socketFactory;

        @Override
        public GitHttpConnection configure(HttpConnection connection, boolean sslVerify) {
            if (!(connection instanceof GitHttpConnection)) {
                throw new RuntimeException("connection type is not " + GitHttpConnection.class.getName());
            }
            return (GitHttpConnection) connection;
        }

        @Override
        public void close() {
            securityContext = null;
            socketFactory = null;
        }
    }
}
