package org.jboss.fuse.tnb.jms.client;

public interface BasicJMSOperations<T> {
    void send(String message);

    default String receive() {
        return receive(30000L);
    }

    String receive(long timeout);

    default T receiveMessage() {
        return receiveMessage(30000L);
    }

    T receiveMessage(long timeout);
}
