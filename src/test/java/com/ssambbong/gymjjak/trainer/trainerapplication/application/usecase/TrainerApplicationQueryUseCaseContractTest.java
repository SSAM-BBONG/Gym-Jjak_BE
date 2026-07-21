package com.ssambbong.gymjjak.trainer.trainerapplication.application.usecase;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class TrainerApplicationQueryUseCaseContractTest {

    @Test
    void 내_트레이너_신청서_목록을_페이지_기준으로_조회한다() {
        boolean hasMyApplicationListMethod = Arrays.stream(
                        TrainerApplicationQueryUseCase.class.getMethods()
                )
                .anyMatch(method ->
                        method.getName().equals("findMyTrainerApplications")
                                && Arrays.equals(
                                method.getParameterTypes(),
                                new Class<?>[]{Long.class, int.class}
                        )
                                && method.getReturnType()
                                .getSimpleName()
                                .equals("MyTrainerApplicationListResult")
                );

        assertThat(hasMyApplicationListMethod).isTrue();
    }

    @Test
    void finds_my_trainer_application_detail_by_application_id() {
        boolean hasMyApplicationDetailMethod = Arrays.stream(
                        TrainerApplicationQueryUseCase.class.getMethods()
                )
                .anyMatch(method ->
                        method.getName().equals("getMyTrainerApplication")
                                && Arrays.equals(
                                method.getParameterTypes(),
                                new Class<?>[]{Long.class, Long.class}
                        )
                );

        assertThat(hasMyApplicationDetailMethod).isTrue();
    }
}
