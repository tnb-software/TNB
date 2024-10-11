package software.tnb.db.dballocator.configuration;

import software.tnb.common.config.Configuration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DbAllocatorConfiguration extends Configuration {

    public static final String LABEL = "dballocator.label";
    public static final String DEPENDENCIES = "dballocator.dependencies";
    public static final String REQUESTEE = "dballocator.requestee";
    public static final String EXPIRE = "dballocator.expire";
    public static final String ERASE = "dballocator.erase";
    public static final String URL = "dballocator.url";

    public static String getLabel() {
        return getProperty(LABEL, "postgresql145");
    }

    public static String[] getDependencies() {
        return getArray(DEPENDENCIES);
    }

    public static String getRequestee() {
        return getProperty(REQUESTEE, DbAllocatorConfiguration.class.getPackageName());
    }

    public static int getExpire() {
        return getInteger(EXPIRE, 60);
    }

    public static boolean getErase() {
        return getBoolean(ERASE, true);
    }

    public static String getUrl() {
        return getProperty(URL);
    }

    public static String getType() {
        final Pattern r = Pattern.compile("^(db2|[a-z]+)");
        final Matcher m = r.matcher(getLabel());

        if (m.find()) {
            return m.group(0);
        }
        return null;
    }
}
