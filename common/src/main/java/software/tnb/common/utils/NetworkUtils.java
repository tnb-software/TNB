package software.tnb.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class NetworkUtils {
    private static final Logger LOG = LoggerFactory.getLogger(NetworkUtils.class);
    private static final Set<Integer> ALLOCATED_PORTS = ConcurrentHashMap.newKeySet();

    private NetworkUtils() {
    }

    public static int getFreePort() {
        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(0)) {
                int port = serverSocket.getLocalPort();
                if (ALLOCATED_PORTS.contains(port)) {
                    LOG.trace("Waiting, port {} already allocated", port);
                    WaitUtils.sleep(500L);
                } else {
                    LOG.trace("Allocated free port {}", port);
                    ALLOCATED_PORTS.add(port);
                    return port;
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to allocate a new port", e);
            }
        }
    }

    public static void releasePort(int port) {
        LOG.trace("Releasing port {}", port);
        ALLOCATED_PORTS.remove(port);
    }
}
