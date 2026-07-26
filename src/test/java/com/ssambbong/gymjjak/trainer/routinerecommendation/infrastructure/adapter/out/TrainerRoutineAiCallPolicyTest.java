package com.ssambbong.gymjjak.trainer.routinerecommendation.infrastructure.adapter.out;

import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrainerRoutineAiCallPolicyTest {

    @Test
    void retriesOnlyConnectionFailureOnce() {
        TrainerRoutineAiCallPolicy policy = new TrainerRoutineAiCallPolicy();
        AtomicInteger attempts = new AtomicInteger();

        String result = policy.execute(() -> {
            if (attempts.incrementAndGet() == 1) {
                throw new TrainerRoutineAiCallPolicy.ConnectionFailure(new ConnectException("refused"));
            }
            return "recommended";
        });

        assertThat(result).isEqualTo("recommended");
        assertThat(attempts).hasValue(2);
    }

    @Test
    void doesNotRetryResponseFailure() {
        TrainerRoutineAiCallPolicy policy = new TrainerRoutineAiCallPolicy();
        AtomicInteger attempts = new AtomicInteger();

        assertThatThrownBy(() -> policy.execute(() -> {
            attempts.incrementAndGet();
            throw new TrainerRoutineAiCallPolicy.ResponseFailure();
        })).isInstanceOf(TrainerRoutineAiCallPolicy.ResponseFailure.class);

        assertThat(attempts).hasValue(1);
    }

    @Test
    void opensCircuitAfterThreeFailuresAndSkipsTheNextFastApiCall() {
        TrainerRoutineAiCallPolicy policy = new TrainerRoutineAiCallPolicy();

        for (int index = 0; index < 3; index++) {
            assertThatThrownBy(() -> policy.execute(() -> {
                throw new TrainerRoutineAiCallPolicy.ResponseFailure();
            })).isInstanceOf(TrainerRoutineAiCallPolicy.ResponseFailure.class);
        }

        AtomicInteger attempts = new AtomicInteger();

        assertThatThrownBy(() -> policy.execute(() -> {
            attempts.incrementAndGet();
            return "unreachable";
        })).isInstanceOf(TrainerRoutineAiCallPolicy.CircuitOpenFailure.class);

        assertThat(attempts).hasValue(0);
    }
}
