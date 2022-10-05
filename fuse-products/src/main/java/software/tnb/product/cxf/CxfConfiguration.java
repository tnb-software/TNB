package software.tnb.product.cxf;

import software.tnb.common.config.Configuration;

public class CxfConfiguration extends Configuration {
    public static final String CXF_VERSION = "cxf.version";

    public static String cxfVersion() {
        return getProperty(CXF_VERSION, "3.4.8.redhat-00033");
    }
}
