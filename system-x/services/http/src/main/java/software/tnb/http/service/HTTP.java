package software.tnb.http.service;

import software.tnb.common.account.NoAccount;
import software.tnb.common.client.NoClient;
import software.tnb.common.deployment.WithDockerImage;
import software.tnb.common.service.Service;
import software.tnb.common.validation.NoValidation;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public abstract class HTTP extends Service<NoAccount, NoClient, NoValidation> implements WithDockerImage {

    public static final int HTTP_PORT = 8080;
    public static final int HTTPS_PORT = 8443;

    public abstract String httpUrl();

    public abstract String httpsUrl();

    public abstract String getLog();

    public abstract String getHost();

    public abstract int getHttpPort();

    public abstract int getHttpsPort();

    public byte[] getSignature() {
        byte[] signature;
        try (InputStream is = HTTP.class.getResource("/http-echo/fullchain.pem").openStream()) {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
            signature = cer.getSignature();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return signature;
    }

    public String defaultImage() {
        return "quay.io/fuse_qe/http-https-echo:latest";
    }
}
