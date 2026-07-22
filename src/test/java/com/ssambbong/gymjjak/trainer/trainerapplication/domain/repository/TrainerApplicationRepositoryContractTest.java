package com.ssambbong.gymjjak.trainer.trainerapplication.domain.repository;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TrainerApplicationRepositoryContractTest {

    @Test
    void 사용자와_요청_조직_목록으로_중복_신청을_확인한다() {
        boolean hasDuplicateLookupMethod = Arrays.stream(
                        TrainerApplicationRepository.class.getMethods()
                )
                .anyMatch(method ->
                        method.getName().equals(
                                "existsDuplicateBlockingApplicationByUserIdAndOrganizationIds"
                        )
                                && Arrays.equals(
                                method.getParameterTypes(),
                                new Class<?>[]{Long.class, List.class}
                        )
                                && method.getReturnType().equals(boolean.class)
                );

        assertThat(hasDuplicateLookupMethod).isTrue();
    }
}
