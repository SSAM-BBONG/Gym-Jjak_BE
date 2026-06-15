package com.ssambbong.gymjjak.trainer.trainerapplication.domain.exception;

import com.ssambbong.gymjjak.global.domain.common.exception.NotFoundException;

public class TrainerApplicationNotFoundException extends NotFoundException {

    public TrainerApplicationNotFoundException(Long trainerApplicationId) {
        super(TrainerApplicationErrorCode.TRAINER_APPLICATION_NOT_FOUND);
        addContext("trainerApplicationId", trainerApplicationId);
    }

    private TrainerApplicationNotFoundException() {
        super(TrainerApplicationErrorCode.TRAINER_APPLICATION_NOT_FOUND);
    }

    // userId로 trainerApplicationId를 조회할 때 발생하는 예외 처리
    public static TrainerApplicationNotFoundException byUserId(Long userId) {
        TrainerApplicationNotFoundException exception =
                new TrainerApplicationNotFoundException();

        exception.addContext("userId", userId);
        return exception;
    }
}