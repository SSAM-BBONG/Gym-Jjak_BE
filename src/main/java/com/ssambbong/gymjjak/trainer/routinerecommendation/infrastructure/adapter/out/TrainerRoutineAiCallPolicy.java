package com.ssambbong.gymjjak.trainer.routinerecommendation.infrastructure.adapter.out;

import java.time.Clock;
import java.time.Instant;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

// Isolates synchronous FastAPI failures without retrying requests that may have reached the LLM.
public class TrainerRoutineAiCallPolicy {
    private static final int MAX_CONSECUTIVE_FAILURES = 3;
    private static final Duration OPEN_DURATION = Duration.ofSeconds(30);

    private final Clock clock;
    private final AtomicInteger consecutiveFailures = new AtomicInteger();
    private volatile Instant openUntil = Instant.MIN;

    public TrainerRoutineAiCallPolicy() {
        this(Clock.systemUTC());
    }

    TrainerRoutineAiCallPolicy(Clock clock) {
        this.clock = clock;
    }

    // Retries only failures that prove the FastAPI connection was not established.
    public <T> T execute(Supplier<T> operation) {
        if (Instant.now(clock).isBefore(openUntil)) {
            throw new CircuitOpenFailure();
        }
        try {
            T result = operation.get();
            consecutiveFailures.set(0);
            return result;
        } catch (ConnectionFailure firstFailure) {
            return retryConnectionOnce(operation, firstFailure);
        } catch (ResponseFailure failure) {
            recordFailure();
            throw failure;
        }
    }

    // A second connection failure opens the circuit only when the configured threshold is reached.
    private <T> T retryConnectionOnce(Supplier<T> operation, ConnectionFailure firstFailure) {
        try {
            T result = operation.get();
            consecutiveFailures.set(0);
            return result;
        } catch (ConnectionFailure retryFailure) {
            recordFailure();
            throw retryFailure;
        } catch (ResponseFailure responseFailure) {
            recordFailure();
            throw responseFailure;
        }
    }

    // Prevents repeated external calls while the dependency is known to be unavailable.
    private void recordFailure() {
        if (consecutiveFailures.incrementAndGet() >= MAX_CONSECUTIVE_FAILURES) {
            openUntil = Instant.now(clock).plus(OPEN_DURATION);
            consecutiveFailures.set(0);
        }
    }

    static class ConnectionFailure extends RuntimeException {
        ConnectionFailure(Throwable cause) { super(cause); }
    }

    static class ResponseFailure extends RuntimeException {
        ResponseFailure() { super(); }
        ResponseFailure(Throwable cause) { super(cause); }
    }

    static class CircuitOpenFailure extends RuntimeException {
    }
}
