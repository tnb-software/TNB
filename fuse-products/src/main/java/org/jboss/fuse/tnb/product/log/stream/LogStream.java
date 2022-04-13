package org.jboss.fuse.tnb.product.log.stream;

public interface LogStream {
    static String marker(String integrationName) {
        return marker(integrationName, null);
    }

    static String marker(String integrationName, String phase) {
        return (phase == null || phase.isEmpty()) ? String.format("[%s]", integrationName) : String.format("[%s][%s]", integrationName, phase);
    }

    void stop();
}
