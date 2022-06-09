package software.tnb.http.service;

import software.tnb.common.service.Service;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public abstract class HTTP implements Service {

    public static final int HTTP_PORT = 8080;
    public static final int HTTPS_PORT = 8443;
    private static final String HTTP_IMAGE_NAME_PROPERTY = "http.image";

    public abstract String httpUrl();

    public abstract String httpsUrl();

    public abstract String getLog();

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

    protected String httpImage() {
        return System.getProperty(HTTP_IMAGE_NAME_PROPERTY, "quay.io/fuse_qe/http-https-echo:latest");
    }
}
