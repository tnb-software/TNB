package software.tnb.product.ck.log;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * For tailing the camel-k operator logs, we are interested only in the lines created after the integration object was created.
 *
 * This parses the "ts":"123456.123456" part of the camel-k log and compares the timestamps
 */
@Plugin(name = "CamelKOperatorLogFilter", category = "Core", elementType = "filter")
public final class CamelKOperatorLogFilter extends AbstractFilter {
    private static final Pattern REGEX = Pattern.compile(".*\"ts\":(\\d+).\\d+.*");

    private CamelKOperatorLogFilter(Result onMatch, Result onMismatch) {
        super(onMatch, onMismatch);
    }

    @PluginFactory
    public static CamelKOperatorLogFilter createFilter(
        @PluginAttribute(value = "onMatch", defaultString = "ACCEPT") Result onMatch,
        @PluginAttribute(value = "onMismatch", defaultString = "DENY") Result onMismatch) {
        return new CamelKOperatorLogFilter(onMatch, onMismatch);
    }

    @Override
    public Result filter(LogEvent event) {
        Matcher m = REGEX.matcher(event.getMessage().getFormattedMessage());

        if (m.find()) {
            Instant timestamp = Instant.ofEpochSecond(Long.parseLong(m.group(1)));
            // This is set in OpenshiftLogStream for each line streamed (only Instant.toEpochMilli() is present)
            Instant startTimestamp = Instant.ofEpochMilli(Long.parseLong(event.getMarker().getParents()[0].getName()));
            return timestamp.isAfter(startTimestamp) ? onMatch : onMismatch;
        } else {
            return onMatch;
        }
    }
}
