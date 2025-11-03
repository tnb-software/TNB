package software.tnb.ftp.sftp.service.configuration;

import software.tnb.common.service.configuration.ServiceConfiguration;

public class SFTPConfiguration extends ServiceConfiguration {
    private static final String ENABLE_CERTIFICATE_AUTH = "sftp.enable.certificate.auth";

    public SFTPConfiguration enableCertificateAuth(boolean value) {
        set(ENABLE_CERTIFICATE_AUTH, value);
        return this;
    }

    public boolean isCertificateAuthEnabled() {
        Boolean value = get(ENABLE_CERTIFICATE_AUTH, Boolean.class);
        return value != null ? value : false;
    }
}
