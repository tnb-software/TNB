package org.jboss.fuse.tnb.common.config;

public class SystemXConfiguration extends Configuration {
    public static final String MONGODB_IMAGE = "mongodb.image";

    public static String mongoDbImage() {
        return getProperty(MONGODB_IMAGE, "quay.io/bitnami/mongodb:4.4.5");
    }

}
