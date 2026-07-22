package com.ssambbong.gymjjak.trainer.trainerapplication.application.port.out;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TrainerApplicationOrganizationPortContractTest {

    @Test
    void 활성_조직_개수를_조직_ID_목록으로_한번에_조회한다() {
        boolean hasCountMethod = Arrays.stream(
                        TrainerApplicationOrganizationPort.class.getMethods()
                )
                .anyMatch(method ->
                        method.getName().equals("countActiveOrganizationsByIds")
                                && Arrays.equals(
                                method.getParameterTypes(),
                                new Class<?>[]{List.class}
                        )
                                && method.getReturnType().equals(long.class)
                );

        assertThat(hasCountMethod).isTrue();
    }
}
