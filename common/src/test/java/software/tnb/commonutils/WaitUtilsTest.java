package software.tnb.commonutils;

import software.tnb.common.exception.FailureConditionMetException;
import software.tnb.common.exception.TimeoutException;
import software.tnb.common.utils.WaitUtils;

import org.junit.jupiter.api.Test;

import org.assertj.core.api.Assertions;
import org.awaitility.core.ConditionTimeoutException;
import org.awaitility.core.TerminalFailureException;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

class WaitUtilsTest {

    @Test
    void waitForShouldSucceedBeforeTimeout() {
        AtomicInteger counter = new AtomicInteger(0);
        int retries = 10;
        long sleepTime = 200;
        BooleanSupplier check = () -> counter.incrementAndGet() >= retries;

        var now = Instant.now();
        WaitUtils.waitFor(check, retries, sleepTime, "counter to reach " + retries);
        var after = Instant.now();
        Assertions.assertThat(Duration.between(now, after))
            .isGreaterThanOrEqualTo(Duration.ofMillis((retries - 1) * sleepTime))
            .isLessThanOrEqualTo(Duration.ofMillis((retries + 1) * sleepTime));
    }

    @Test
    void waitForShouldFailAfterTimeout() {
        int retries = 10;
        int sleepTime = 200;
        var now = Instant.now();
        Assertions.assertThatThrownBy(() -> WaitUtils.waitFor(() -> false, retries, sleepTime, ""))
            .isInstanceOfAny(ConditionTimeoutException.class, TimeoutException.class);
        var after = Instant.now();
        Assertions.assertThat(Duration.between(now, after))
            .isGreaterThan(Duration.ofMillis(retries * sleepTime));
    }

    @Test
    void waitForWithFailFastShouldFailFast() {
        AtomicInteger counter = new AtomicInteger(0);
        AtomicInteger failCounter = new AtomicInteger(0);
        int failFastAfter = 10;
        int succeedAfter = failFastAfter * 2;
        long sleepTime = 200;
        BooleanSupplier check = () -> counter.incrementAndGet() >= succeedAfter;
        BooleanSupplier failCheck = () -> failCounter.incrementAndGet() >= failFastAfter;

        var now = Instant.now();
        Assertions.assertThatThrownBy(() -> WaitUtils.waitFor(check, failCheck, sleepTime, "counter to reach " + succeedAfter))
            .isInstanceOfAny(TerminalFailureException.class, FailureConditionMetException.class);
        var after = Instant.now();

        Assertions.assertThat(Duration.between(now, after))
            .isGreaterThanOrEqualTo(Duration.ofMillis((failFastAfter - 1) * sleepTime))
            .isLessThanOrEqualTo(Duration.ofMillis((failFastAfter + 1) * sleepTime));
    }

    @Test
    void waitForWithFailFastShouldSucceed() {
        AtomicInteger counter = new AtomicInteger(0);
        AtomicInteger failCounter = new AtomicInteger(0);
        int failFastAfter = 10;
        int succeedAfter = failFastAfter / 2;
        long sleepTime = 200;
        BooleanSupplier check = () -> counter.incrementAndGet() >= succeedAfter;
        BooleanSupplier failCheck = () -> failCounter.incrementAndGet() >= failFastAfter;

        var now = Instant.now();
        WaitUtils.waitFor(check, failCheck, sleepTime, "counter to reach " + succeedAfter);
        var after = Instant.now();

        Assertions.assertThat(Duration.between(now, after))
            .isGreaterThanOrEqualTo(Duration.ofMillis((succeedAfter - 1) * sleepTime))
            .isLessThanOrEqualTo(Duration.ofMillis((succeedAfter + 1) * sleepTime));
    }

    @Test
    void withTimeoutShouldFailAfterTimeout() {
        long timeoutMillis = 2000;

        var now = Instant.now();
        Assertions.assertThatThrownBy(() -> WaitUtils.withTimeout(() -> {
                try {
                    Thread.sleep(timeoutMillis);
                } catch (InterruptedException e) {
                    // no-op
                }
                return true;
            }, Duration.ofMillis(timeoutMillis / 2)))
            .isInstanceOfAny(TimeoutException.class);
        var after = Instant.now();

        Assertions.assertThat(Duration.between(now, after))
            .isGreaterThanOrEqualTo(Duration.ofMillis(timeoutMillis / 2))
            .isLessThanOrEqualTo(Duration.ofMillis(timeoutMillis));
    }

    @Test
    void withTimeoutShouldSucceedBeforeTimeout() {
        long timeoutMillis = 2000;

        var now = Instant.now();
        Boolean result = WaitUtils.withTimeout(() -> {
            try {
                Thread.sleep(timeoutMillis);
            } catch (InterruptedException e) {
                // no-op
            }
            return true;
        }, Duration.ofMillis(timeoutMillis * 2));
        var after = Instant.now();

        Assertions.assertThat(result).isTrue();
        Assertions.assertThat(Duration.between(now, after))
            .isGreaterThanOrEqualTo(Duration.ofMillis(timeoutMillis))
            .isLessThanOrEqualTo(Duration.ofMillis(timeoutMillis * 2));
    }
}
