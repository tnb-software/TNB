package software.tnb.jms.client;

import software.tnb.common.utils.WaitUtils;

import org.apache.commons.lang3.RandomStringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Periodically sends a message to the topic to avoid closing the client due to no activity.
 * <p/>
 * There is an option in both client and broker, but it didn't seem to have any effect and the connection was terminated after 60 seconds of
 * no activity.
 */
public class MQTTKeepAlive {
    private final String topic = "keepalive" + RandomStringUtils.randomAlphabetic(10);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Starts the keep alive thread for the client.
     * <p>
     * Since mqtt and mqtt5 clients do not have anything in common, the method is invoked via reflection.
     *
     * @param client
     */
    protected void startKeepAlive(Object client) {
        executor.submit((Runnable) () -> {
            try {
                final Method publish = client.getClass().getMethod("publish", String.class, byte[].class, int.class, boolean.class);

                while (true) {
                    WaitUtils.sleep(30000L);
                    publish.invoke(client, topic, "".getBytes(), 0, false);
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to send keepalive message", e);
            }
        });
    }

    protected void stopKeepAlive() {
        executor.shutdownNow();
    }
}
