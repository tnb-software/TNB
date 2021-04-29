package org.jboss.fuse.tnb.common.config;

public class SystemXConfiguration extends Configuration {
    public static final String MONGODB_IMAGE = "mongodb.image";
    public static final String FTP_IMAGE = "ftp.image";
    public static final String SFTP_IMAGE = "sftp.image";

    public static String mongoDbImage() {
        return getProperty(MONGODB_IMAGE, "quay.io/bitnami/mongodb:4.4.5");
    }

    public static String ftpImage() {
        // TODO: move this to a team org
        return getProperty(FTP_IMAGE, "quay.io/asmigala/ftpserver:latest");
    }

    public static String sftpImage() {
        return getProperty(FTP_IMAGE, "atmoz/sftp:alpine");
    }
}
