package org.jboss.fuse.tnb.http.service;

import org.jboss.fuse.tnb.common.service.Service;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public abstract class HttpService implements Service {

    private static final String HTTP_IMAGE_NAME_PROPERTY = "http.image";

    public abstract String httpUrl();

    public abstract String httpsUrl();

    public byte[] getSignature() {
        byte[] signature;
        try (InputStream is = HttpService.class.getResource("/httpbin/server.cert").openStream()) {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
            signature = cer.getSignature();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return signature;
    }

    protected String httpImage() {
        return System.getProperty(HTTP_IMAGE_NAME_PROPERTY, "quay.io/syndesis_qe/httpbin:latest");
    }
}
