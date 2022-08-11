package software.tnb.common.utils;

import java.net.ServerSocket;

public final class NetworkUtils {
    private NetworkUtils() {
    }

    public static int getFreePort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (Exception e) {
            throw new RuntimeException("Unable to allocate free port", e);
        }
    }
}
