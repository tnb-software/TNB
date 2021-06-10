package org.jboss.fuse.tnb.common.service;

import org.jboss.fuse.tnb.common.config.OpenshiftConfiguration;
import org.jboss.fuse.tnb.common.deployment.OpenshiftDeployable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public final class ServiceFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceFactory.class);

    private ServiceFactory() {
    }

    /**
     * Returns an instance of subclass of given type. If there is just one implementation, it returns that one, otherwise it will return
     * a specific implementation for given environment (local / openshift)
     *
     * @param clazz class to create
     * @param <S> type
     */
    public static <S extends Service> S create(Class<S> clazz) {
        final ServiceLoader<S> loader = ServiceLoader.load(clazz);

        if (loader.stream().count() == 0) {
            LOG.error("No Service class implementation for class {} found!", clazz.getSimpleName());
            throw new IllegalArgumentException();
        }

        // If there is just one implementation, return that one
        if (loader.stream().count() == 1) {
            return loader.findFirst().get();
        }

        // If there are multiple, decide which one should be returned
        final Optional<S> service = StreamSupport.stream(loader.spliterator(), false)
            .filter(s -> s instanceof OpenshiftDeployable == OpenshiftConfiguration.isOpenshift())
            .findFirst();
        if (service.isEmpty()) {
            LOG.error("No Service class implementation for class {} / environment {} found!",
                clazz.getSimpleName(), OpenshiftConfiguration.isOpenshift() ? "openshift" : "local");
            throw new IllegalArgumentException();
        } else {
            return service.get();
        }
    }
}
