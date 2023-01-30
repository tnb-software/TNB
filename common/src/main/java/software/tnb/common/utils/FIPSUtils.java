package software.tnb.common.utils;

import java.security.Security;
import java.util.Arrays;

public final class FIPSUtils {

    private FIPSUtils() {
    }

    public static boolean isFipsEnabled() {
        return Arrays.stream(Security.getProviders())
            .anyMatch(provider -> provider.getName().toUpperCase().contains("FIPS"));
    }
}
