package software.tnb.product.junit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE, METHOD})
@Retention(RUNTIME)
@DisabledIfEnvironmentVariable(named = "DOCKER_HOST", matches = ".*podman.*", disabledReason = "The test doesn't support Podman")
public @interface SkipOnPodman {
}
