package software.tnb.common.utils;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Helper for hiding values of sensitive properties (tokens, passwords, secrets, ...) before they are written to a log.
 * <p/>
 * A property is considered sensitive when its key matches {@link #SENSITIVE_KEY}. The check is deliberately conservative - it is better to hide a
 * harmless value than to leak a credential.
 */
public final class SensitiveValues {
    public static final String MASK = "******";

    private static final Pattern SENSITIVE_KEY = Pattern.compile(
        "passw(or)?d|passphrase|pwd|secret|token|api[-_.]?key|access[-_.]?key|private[-_.]?key|authorization|session[-_.]?string|app[-_.]?hash",
        Pattern.CASE_INSENSITIVE);

    // matches "-Dkey=value" command line arguments
    private static final Pattern SYSTEM_PROPERTY_ARG = Pattern.compile("(-D)([^\\s=]+)=(\\S*)");

    private SensitiveValues() {
    }

    public static boolean isSensitive(String key) {
        return key != null && SENSITIVE_KEY.matcher(key).find();
    }

    /**
     * Returns the value, or {@link #MASK} if the key is sensitive.
     *
     * @param key property key
     * @param value property value
     * @return value that is safe to log
     */
    public static String mask(String key, String value) {
        return isSensitive(key) ? MASK : value;
    }

    /**
     * Hides the values of sensitive "-Dkey=value" arguments in the given command line string.
     *
     * @param args space-separated arguments
     * @return arguments that are safe to log
     */
    public static String maskArguments(String args) {
        if (args == null) {
            return null;
        }
        return SYSTEM_PROPERTY_ARG.matcher(args).replaceAll(match ->
            Matcher.quoteReplacement(match.group(1) + match.group(2) + "=" + mask(match.group(2), match.group(3))));
    }

    /**
     * Dumps the properties as new-line separated string of key=value, with the values of sensitive properties hidden.
     *
     * @param properties properties
     * @return string that is safe to log
     */
    public static String toMaskedString(Properties properties) {
        return properties.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + mask(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())))
            .collect(Collectors.joining(System.lineSeparator()));
    }
}
