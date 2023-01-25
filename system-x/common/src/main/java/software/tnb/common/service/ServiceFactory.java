package software.tnb.common.service;

import software.tnb.common.config.OpenshiftConfiguration;
import software.tnb.common.deployment.Deployable;
import software.tnb.common.deployment.OpenshiftDeployable;

import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.util.ReflectionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Consumer;
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
        if (ReflectionUtils.isAbstract(clazz) || clazz.isInterface()) {
            final ServiceLoader<S> loader = ServiceLoader.load(clazz);
            if (loader.stream().findAny().isEmpty()) {
                LOG.error("No Service class implementation for class {} found!", clazz.getSimpleName());
                throw new IllegalArgumentException();
            }

            // If there is just one implementation, return that one
            if (loader.stream().count() == 1) {
                return loader.findFirst().get();
            }

            // If there are multiple, decide which one should be returned
            final Optional<S> service = StreamSupport.stream(loader.spliterator(), false)
                .filter(s -> {
                    if (OpenshiftConfiguration.isOpenshift()) {
                        return s instanceof OpenshiftDeployable || s.getClass().getSimpleName().toLowerCase().contains("openshift");
                    } else {
                        return s instanceof Deployable || s.getClass().getSimpleName().toLowerCase().contains("local");
                    }
                })
                .findFirst();

            if (service.isEmpty()) {
                LOG.error("No Service class implementation for class {} / environment {} found!",
                    clazz.getSimpleName(), OpenshiftConfiguration.isOpenshift() ? "openshift" : "local");
                throw new IllegalArgumentException();
            } else {
                return service.get();
            }
        } else {
            return Try.call(() -> ReflectionUtils.newInstance(clazz))
                .getOrThrow((e) -> new IllegalArgumentException("Failed to instantiate class " + clazz.getSimpleName(), e));
        }
    }

    public static <S extends Service> void withService(Class<S> clazz, Consumer<S> code) {
        S service = create(clazz);
        try {
            service.beforeAll(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        code.accept(service);

        try {
            service.afterAll(null);
        } catch (Exception e) {
            LOG.warn("Exception thrown while undeploying service", e);
        }
    }
}
