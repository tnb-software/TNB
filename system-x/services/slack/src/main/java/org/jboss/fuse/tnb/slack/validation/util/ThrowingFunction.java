package org.jboss.fuse.tnb.slack.validation.util;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
    R apply(T t) throws Exception;
}
