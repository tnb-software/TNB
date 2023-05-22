package software.tnb.product.git.support;

import software.tnb.common.utils.HTTPUtils;

import org.eclipse.jgit.transport.http.HttpConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GitHttpConnection  implements HttpConnection {
    HttpURLConnection delegate;

    public GitHttpConnection(URL url) throws IOException {
        delegate = (HttpURLConnection) url.openConnection();
    }

    public GitHttpConnection(URL url, Proxy proxy) throws IOException {
        delegate = (HttpURLConnection) url.openConnection(proxy);
    }

    @Override
    public int getResponseCode() throws IOException {
        return delegate.getResponseCode();
    }

    @Override
    public URL getURL() {
        return delegate.getURL();
    }

    @Override
    public String getResponseMessage() throws IOException {
        return delegate.getResponseMessage();
    }

    @Override
    public Map<String, List<String>> getHeaderFields() {
        return delegate.getHeaderFields();
    }

    @Override
    public void setRequestProperty(String key, String value) {
        delegate.setRequestProperty(key, value);
    }

    @Override
    public void setRequestMethod(String method) throws ProtocolException {
        delegate.setRequestMethod(method);
    }

    @Override
    public void setUseCaches(boolean usecaches) {
        delegate.setUseCaches(usecaches);
    }

    @Override
    public void setConnectTimeout(int timeout) {
        delegate.setConnectTimeout(timeout);
    }

    @Override
    public void setReadTimeout(int timeout) {
        delegate.setReadTimeout(timeout);
    }

    @Override
    public String getContentType() {
        return delegate.getContentType();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return delegate.getInputStream();
    }

    @Override
    public String getHeaderField(String name) {
        return delegate.getHeaderField(name);
    }

    @Override
    public List<String> getHeaderFields(String name) {
        return delegate.getHeaderFields().get(name);
    }

    @Override
    public int getContentLength() {
        return delegate.getContentLength();
    }

    @Override
    public void setInstanceFollowRedirects(boolean followRedirects) {
        delegate.setInstanceFollowRedirects(followRedirects);
    }

    @Override
    public void setDoOutput(boolean dooutput) {
        delegate.setDoOutput(dooutput);
    }

    @Override
    public void setFixedLengthStreamingMode(int contentLength) {
        delegate.setFixedLengthStreamingMode(contentLength);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return delegate.getOutputStream();
    }

    @Override
    public void setChunkedStreamingMode(int chunklen) {
        delegate.setChunkedStreamingMode(chunklen);
    }

    @Override
    public String getRequestMethod() {
        return delegate.getRequestMethod();
    }

    @Override
    public boolean usingProxy() {
        return delegate.usingProxy();
    }

    @Override
    public void connect() throws IOException {
        delegate.connect();
    }

    @Override
    public void configure(KeyManager[] km, TrustManager[] tm, SecureRandom random) throws NoSuchAlgorithmException, KeyManagementException {
        //the configuration is managed by HTTPUtils
        Optional.of(delegate)
            .filter(d -> d instanceof HttpsURLConnection)
            .map(d -> (HttpsURLConnection) d)
            .orElseThrow(() -> new RuntimeException("unable to apply ssl context"))
            .setSSLSocketFactory(HTTPUtils.getSslContext().getSocketFactory());
    }

    @Override
    public void setHostnameVerifier(HostnameVerifier hostnameverifier) throws NoSuchAlgorithmException, KeyManagementException {
        Optional.of(delegate)
            .filter(d -> d instanceof HttpsURLConnection)
            .map(d -> (HttpsURLConnection) d)
            .orElseThrow(() -> new RuntimeException("unable to apply hostname verifier"))
            .setHostnameVerifier(hostnameverifier);
    }
}
